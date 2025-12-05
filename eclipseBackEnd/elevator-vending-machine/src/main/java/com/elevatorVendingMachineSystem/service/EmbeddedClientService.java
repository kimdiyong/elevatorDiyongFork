package com.elevatorVendingMachineSystem.service;

import com.elevatorVendingMachineSystem.domain.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

@Slf4j
@Service
public class EmbeddedClientService {

    @Value("${embedded.host:192.168.45.246}")   // 노트북2 IP
    private String embeddedHost;

    @Value("${embedded.port:5000}")           // 노트북2 서버 포트
    private int embeddedPort;

    //결제 성공 후 노트북2에 출고 명령 전송
    public void sendDispenseCommand(Product product) {
        String message = product.getName() + "," + product.getLocationCode(); // 예: "콜라,3"

        try (Socket socket = new Socket(embeddedHost, embeddedPort);
             OutputStream os = socket.getOutputStream();
             PrintWriter writer = new PrintWriter(os, true)) {

            writer.println(message);
            log.info("임베디드부에 출고 명령 전송 완료: {}", message);

        } catch (IOException e) {
            log.error("임베디드부 전송 실패: {}", e.getMessage(), e);
        }
    }
}
