package bm.b0b0b0.SoulPact.clan.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public final class ClanHomePasswordHasher {

    private ClanHomePasswordHasher() {
    }

    public static String hash(String password) {
        if (password == null || password.isBlank()) {
            return "";
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashed);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 unavailable", exception);
        }
    }

    public static boolean matches(String password, String storedHash) {
        if (storedHash == null || storedHash.isBlank()) {
            return true;
        }
        return hash(password).equals(storedHash);
    }
}
