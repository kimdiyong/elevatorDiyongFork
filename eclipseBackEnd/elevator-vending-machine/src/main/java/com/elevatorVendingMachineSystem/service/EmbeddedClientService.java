package com.elevatorVendingMachineSystem.service;

import com.fasterxml.jackson.databind.ObjectMapper;  //!여기수정! JSON 변환용 라이브러리
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class EmbeddedClientService {

    @Value("${embedded.host:192.168.45.176}")   // 노트북2 IP
    private String embeddedHost;

    @Value("${embedded.port:5000}")             // 노트북2 서버 포트
    private int embeddedPort;

    private final ObjectMapper objectMapper = new ObjectMapper();  


    
    public void sendDispenseCommand(String productName, int locationCode) {
        String message = productName + "," + locationCode;

        try (Socket socket = new Socket(embeddedHost, embeddedPort);
             OutputStream os = socket.getOutputStream();
             PrintWriter writer = new PrintWriter(os, true)) {

            writer.println(message);
            log.info("임베디드부에 기본 출고 명령 전송 완료: {}", message);

        } catch (IOException e) {
            log.error("임베디드부 전송 실패: {}", e.getMessage(), e);
        }
    }


    
    public void sendReceiptJson(String productName,
                                int locationCode,
                                int price,
                                int receivedAmount,
                                int change) {


        Map<String, Object> jsonData = new HashMap<>();
        jsonData.put("type", "receipt");
        jsonData.put("productName", productName);
        jsonData.put("location", locationCode);
        jsonData.put("price", price);
        jsonData.put("receivedAmount", receivedAmount);
        jsonData.put("change", change);
        jsonData.put("timestamp", System.currentTimeMillis());

        try {
            String jsonString = objectMapper.writeValueAsString(jsonData);

            try (Socket socket = new Socket(embeddedHost, embeddedPort);
                 OutputStream os = socket.getOutputStream();
                 PrintWriter writer = new PrintWriter(os, true)) {

                writer.println(jsonString);   //!여기수정! JSON으로 전송
                log.info("임베디드부에 영수증 JSON 전송 완료: {}", jsonString);

            }
        } catch (Exception e) {
            log.error("임베디드 JSON 전송 실패: {}", e.getMessage(), e);
        }
    }
}
