package cripto;

import model.Credenciais;
import model.TipoDeUsuario;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

public class HashWithSalt {
    public static String getHashSenhaSegura(Credenciais conta)
            throws NoSuchAlgorithmException,
            InvalidKeyException,InvalidKeySpecException {
        int iteracoes = 500000;
        char[] caracteresSenha = conta.getSenha().toCharArray();
        if(conta.getSalt() == null){
            conta.setSalt(getSalt());
        }
        PBEKeySpec spec =
                new PBEKeySpec(caracteresSenha, conta.getSalt(), iteracoes, 512);
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
}
