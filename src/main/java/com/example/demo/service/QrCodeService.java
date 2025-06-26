package com.example.demo.service;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

import org.springframework.stereotype.Service;

import net.glxn.qrgen.javase.QRCode;

@Service
public class QrCodeService {
    public String generateQrCodeAsBase64(String text) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            QRCode.from(text).withSize(250, 250).writeTo(bos);
            String base64 = Base64.getEncoder().encodeToString(bos.toByteArray());
            return "data:image/png;base64," + base64;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}