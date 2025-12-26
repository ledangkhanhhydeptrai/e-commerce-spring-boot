package com.example.demo.payos;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class PayOSSignatureUtil {

    public static String sign(
            int amount,
            String cancelUrl,
            String description,
            long orderCode,
            String returnUrl,
            String checksumKey
    ) {
        try {
            // ⚠️ THỨ TỰ PHẢI CHUẨN PAYOS
            String rawData =
                    "amount=" + amount +
                            "&cancelUrl=" + cancelUrl +
                            "&description=" + description +
                            "&orderCode=" + orderCode +
                            "&returnUrl=" + returnUrl;

            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey =
                    new SecretKeySpec(checksumKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKey);

            byte[] hash = mac.doFinal(rawData.getBytes(StandardCharsets.UTF_8));

            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }

            // 🔥 DEBUG CỰC QUAN TRỌNG
            System.out.println("PAYOS RAW STRING = " + rawData);
            System.out.println("PAYOS SIGNATURE  = " + hex);

            return hex.toString();

        } catch (Exception e) {
            throw new RuntimeException("Sign PayOS error", e);
        }
    }

    public static boolean verifyCallback(String rawData, String checksumKey, String signature) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(checksumKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKey);

            byte[] hash = mac.doFinal(rawData.getBytes(StandardCharsets.UTF_8));

            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b & 0xff));
            }

            String generatedSignature = hex.toString();
            System.out.println("Generated signature: " + generatedSignature);

            return generatedSignature.equals(signature.toLowerCase());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
