package com.sda.project.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class JwtUtil {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("\"sub\"\\s*:\\s*\"([^\"]+)\"");
    private static final Pattern EXP_PATTERN = Pattern.compile("\"exp\"\\s*:\\s*(\\d+)");

    @Value("${jwt.secret:streamflix-development-secret-change-me}")
    private String secret;

    @Value("${jwt.expiration.ms:86400000}")
    private long expirationMs;

    public String generateToken(String email, Long userId, String role) {
        long nowSeconds = Instant.now().getEpochSecond();
        long expirationSeconds = nowSeconds + (expirationMs / 1000);
        String header = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
        String payload = "{\"sub\":\"" + escape(email) + "\",\"userId\":" + userId
                + ",\"role\":\"" + escape(role) + "\",\"iat\":" + nowSeconds
                + ",\"exp\":" + expirationSeconds + "}";
        String encodedHeader = base64Url(header.getBytes(StandardCharsets.UTF_8));
        String encodedPayload = base64Url(payload.getBytes(StandardCharsets.UTF_8));
        String signingInput = encodedHeader + "." + encodedPayload;
        return signingInput + "." + sign(signingInput);
    }

    public String extractEmail(String token) {
        Matcher matcher = EMAIL_PATTERN.matcher(decodePayload(token));
        if (!matcher.find()) {
            throw new IllegalArgumentException("JWT subject is missing");
        }
        return matcher.group(1);
    }

    public boolean validateToken(String token) {
        try {
            String[] parts = splitToken(token);
            String expectedSignature = sign(parts[0] + "." + parts[1]);
            if (!constantTimeEquals(expectedSignature, parts[2])) {
                return false;
            }
            Matcher matcher = EXP_PATTERN.matcher(decodePayload(token));
            return matcher.find() && Long.parseLong(matcher.group(1)) > Instant.now().getEpochSecond();
        } catch (RuntimeException ex) {
            return false;
        }
    }

    private String decodePayload(String token) {
        return new String(Base64.getUrlDecoder().decode(splitToken(token)[1]), StandardCharsets.UTF_8);
    }

    private String[] splitToken(String token) {
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid JWT");
        }
        return parts;
    }

    private String sign(String signingInput) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return base64Url(mac.doFinal(signingInput.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to sign JWT", ex);
        }
    }

    private String base64Url(byte[] value) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(value);
    }

    private boolean constantTimeEquals(String left, String right) {
        return MessageDigestCompat.isEqual(left.getBytes(StandardCharsets.UTF_8), right.getBytes(StandardCharsets.UTF_8));
    }

    private String escape(String value) {
        return value == null ? "" : value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private static class MessageDigestCompat {
        static boolean isEqual(byte[] left, byte[] right) {
            if (left.length != right.length) {
                return false;
            }
            int result = 0;
            for (int i = 0; i < left.length; i++) {
                result |= left[i] ^ right[i];
            }
            return result == 0;
        }
    }
}
