package cripto;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

public class HashWithSalt {
    public static String getHashSenhaSegura(String senha)
            throws NoSuchAlgorithmException,
            InvalidKeyException,InvalidKeySpecException {
        int iteracoes = 500000;
        char[] caracteresSenha = senha.toCharArray();
        byte[] salt = getSalt();
        PBEKeySpec spec =
                new PBEKeySpec(caracteresSenha, salt, iteracoes, 512);
        SecretKeyFactory skf = SecretKeyFactory
                .getInstance("PBKDF2WithHmacSHA1");
        byte[] hash = skf.generateSecret(spec).getEncoded();
        return byte2hex(hash);
    }
    private static byte[] getSalt() throws NoSuchAlgorithmException {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return salt;
    }
    public static String byte2hex(byte[] bytes) {
        StringBuilder strHex = new StringBuilder();
        for (byte b : bytes) {
            strHex.append(String.format("%02x", b));
        }
        return strHex.toString();
    }
    public static void execute(String s1, String s2)
            throws NoSuchAlgorithmException,
            InvalidKeyException, InvalidKeySpecException {
        String hashSenha1 = HashWithSalt.getHashSenhaSegura(s1);
        String hashSenha2 = HashWithSalt.getHashSenhaSegura(s2);
        System.out.println("Senha 1: " + hashSenha1);
        System.out.println("Senha 2: " + hashSenha2);
        if (hashSenha1.equals(hashSenha2)) {
            System.out.println("senhas iguais...");
        }else{
            System.out.println("senhas diferentes...");
        }
    }
    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException {
        String senha1 = "senhaboa", senha2 = "senhaboa";
        System.out.println("Hash de com PBKDF2 em Java = \n");
        HashWithSalt.execute(senha1, senha2);
    }
}
