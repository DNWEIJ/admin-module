package dwe.holding.admin.security;

import lombok.extern.slf4j.Slf4j;

import java.security.MessageDigest;

@Slf4j
public class LegacyMd5Encoder {

    public static boolean matches(String raw, String md5Hash) {
        return md5Hash.equals(hash(raw));
    }

    private static String hash(String raw) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(raw.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            log.error("Cannot validate old md5 password: {}", e.getMessage());
            return "";
        }
    }

}