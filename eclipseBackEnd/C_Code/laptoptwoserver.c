#define _CRT_SECURE_NO_WARNINGS
#define _WINSOCK_DEPRECATED_NO_WARNINGS
#include <stdio.h>
#include <winsock2.h>
#include <windows.h>
#pragma comment(lib, "ws2_32.lib")

int main() {
    SetConsoleOutputCP(CP_UTF8);
    SetConsoleCP(CP_UTF8);
    WSADATA wsa;
    SOCKET serverSocket, clientSocket;
    struct sockaddr_in serverAddr, clientAddr;

    int clientAddrSize = sizeof(clientAddr);

    // 1. Winsock 초기화
    printf("Winsock initializing...\n");
    if (WSAStartup(MAKEWORD(2, 2), &wsa) != 0) {
        printf("Failed. Error Code : %d\n", WSAGetLastError());
        return 1;
    }

    // 2. 소켓 생성
    serverSocket = socket(AF_INET, SOCK_STREAM, 0);
    if (serverSocket == INVALID_SOCKET) {
        printf("소켓 생성 실패\n");
        return 1;
    }

    // 3. 서버 주소 설정
    serverAddr.sin_family = AF_INET;
    serverAddr.sin_addr.s_addr = inet_addr("192.168.45.246"); // 노트북2 IP로 설정
    serverAddr.sin_port = htons(5000);

    // 4. 바인드
    if (bind(serverSocket, (struct sockaddr*)&serverAddr, sizeof(serverAddr)) == SOCKET_ERROR) {
        printf("바인드 실패. Error Code : %d\n", WSAGetLastError());
        return 1;
    }

    // 5. 리슨
    listen(serverSocket, 3);
    printf("Server waiting...\n");

    while (1) {
        // 6. 클라이언트 연결 수락
        clientSocket = accept(serverSocket, (struct sockaddr*)&clientAddr, &clientAddrSize);
        if (clientSocket == INVALID_SOCKET) {
            printf("연결 실패.\n");
            continue;
        }

        printf("client connected!\n");

        // 7. 메시지 수신
        char buffer[1024] = { 0 };
        int recvLen = recv(clientSocket, buffer, sizeof(buffer) - 1, 0);
        if (recvLen > 0) {
            buffer[recvLen] = '\0';

            char name[100];
            int location;

            sscanf(buffer, "%[^,],%d", name, &location);

            printf("DisposeRequestedProduct : %s, location : %d\n", name, location);
        }

        // 8. 소켓 종료
        closesocket(clientSocket);
    }

    closesocket(serverSocket);
    WSACleanup();
    return 0;
}
