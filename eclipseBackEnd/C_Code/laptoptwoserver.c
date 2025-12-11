#define _CRT_SECURE_NO_WARNINGS
#define _WINSOCK_DEPRECATED_NO_WARNINGS

#include <stdio.h>
#include <winsock2.h>
#include <windows.h>
#include <fcntl.h>
#include <io.h>
#include <wchar.h>
#include "cJSON.h"

#pragma comment(lib, "ws2_32.lib")

// UTF-8 → UTF-16 변환 함수
void Utf8ToUtf16(const char* utf8, wchar_t* utf16, int maxLen)
{
    MultiByteToWideChar(CP_UTF8, 0, utf8, -1, utf16, maxLen);
}

// wide 출력용 영수증 함수
void printReceiptW(const wchar_t* productName, int price, int receivedAmount, int change)
{
    SYSTEMTIME st;
    GetLocalTime(&st);

    wprintf(L"\n********** [영수증] **********\n");
    wprintf(L"상호명: BCU 컴퍼니 엘리베이터 자판기 1호\n");
    wprintf(L"일  시: %04d-%02d-%02d %02d:%02d:%02d\n",
        st.wYear, st.wMonth, st.wDay, st.wHour, st.wMinute, st.wSecond);
    wprintf(L"------------------------------\n");
    wprintf(L"상품명          단가    수량    금액\n");
    wprintf(L"%-10ls %6d      1   %6d\n", productName, price, price);
    wprintf(L"------------------------------\n");
    wprintf(L"합계 금액:          %10d원\n", price);
    wprintf(L"받은 금액(CASH):   %10d원\n", receivedAmount);
    wprintf(L"거스름돈:           %10d원\n", change);
    wprintf(L"******************************\n\n");
}

int main()
{
    // 콘솔 출력/입력을 UTF-16 유니코드 모드로 변경
    _setmode(_fileno(stdout), _O_U16TEXT);
    _setmode(_fileno(stdin), _O_U16TEXT);

    WSADATA wsa;
    SOCKET serverSocket, clientSocket;
    struct sockaddr_in serverAddr, clientAddr;

    int clientAddrSize = sizeof(clientAddr);

    wprintf(L"Winsock initializing...\n");

    if (WSAStartup(MAKEWORD(2, 2), &wsa) != 0) {
        wprintf(L"WSAStartup 실패: %d\n", WSAGetLastError());
        return 1;
    }

    serverSocket = socket(AF_INET, SOCK_STREAM, 0);
    if (serverSocket == INVALID_SOCKET) {
        wprintf(L"소켓 생성 실패\n");
        return 1;
    }

    serverAddr.sin_family = AF_INET;
    serverAddr.sin_addr.s_addr = htonl(INADDR_ANY);
    serverAddr.sin_port = htons(5000);

    if (bind(serverSocket, (struct sockaddr*)&serverAddr, sizeof(serverAddr)) == SOCKET_ERROR) {
        wprintf(L"바인드 실패: %d\n", WSAGetLastError());
        return 1;
    }

    listen(serverSocket, 3);
    wprintf(L"서버 대기중...\n");

    while (1)
    {
        clientSocket = accept(serverSocket, (struct sockaddr*)&clientAddr, &clientAddrSize);
        if (clientSocket == INVALID_SOCKET) {
            wprintf(L"클라이언트 연결 실패.\n");
            continue;
        }

        wprintf(L"클라이언트 연결 성공!\n");

        char buffer[2048] = { 0 };
        int recvLen = recv(clientSocket, buffer, sizeof(buffer) - 1, 0);
        if (recvLen <= 0) {
            closesocket(clientSocket);
            continue;
        }

        buffer[recvLen] = '\0';

        // JSON 여부 판단
        if (buffer[0] == '{')
        {
            wchar_t wTmp[2048];
            MultiByteToWideChar(CP_UTF8, 0, buffer, -1, wTmp, 2048);
            wprintf(L"JSON 수신: %ls\n", wTmp);



            cJSON* root = cJSON_Parse(buffer);
            if (!root) {
                wprintf(L"JSON 파싱 실패.\n");
                closesocket(clientSocket);
                continue;
            }

            cJSON* productName = cJSON_GetObjectItem(root, "productName");
            cJSON* location = cJSON_GetObjectItem(root, "location");
            cJSON* price = cJSON_GetObjectItem(root, "price");
            cJSON* receivedAmount = cJSON_GetObjectItem(root, "receivedAmount");
            cJSON* change = cJSON_GetObjectItem(root, "change");

            if (productName && location && price && receivedAmount && change)
            {
                wchar_t wName[256];
                Utf8ToUtf16(productName->valuestring, wName, 256);

                wprintf(L"출고 요청 수신 → 상품명: %ls, 위치: %d\n",
                    wName, location->valueint);

                printReceiptW(wName, price->valueint, receivedAmount->valueint, change->valueint);
            }

            cJSON_Delete(root);
        }
        else
        {
            // "콜라,3" 같은 ANSI 메시지는 여기서 처리한다. (필요 시 UTF-8 변환 추가)
        }

        closesocket(clientSocket);
    }

    closesocket(serverSocket);
    WSACleanup();
    return 0;
}
