package tech.mathai.app.utils;

import at.favre.lib.crypto.bcrypt.BCrypt;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;


public class CryptUtil {

    public static String hash(String username) throws NoSuchAlgorithmException {
        SecureRandom random = SecureRandom.getInstance("NativePRNGNonBlocking");
        BCrypt.Hasher hasher = BCrypt.with(random);
        return new String(hasher.hash(5, username.getBytes()));
    }

    public static boolean verify(String pass, String username) {
        BCrypt.Verifyer verifyer = BCrypt.verifyer();
        return verifyer.verify(username.getBytes(), pass.getBytes()).verified;
    }
}
