package cripto;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class HMAC {
    public static final String ALGORITMO = "HmacSHA256";


    public static String hMac(String chave, String mensagem) throws NoSuchAlgorithmException, IOException, InvalidKeyException {
        Mac shaHMAC = Mac.getInstance(ALGORITMO);
        SecretKeySpec chaveMAC = new SecretKeySpec(chave.getBytes("UTF-8"), ALGORITMO);
        shaHMAC.init(chaveMAC);
        byte[] bytesHMAC = shaHMAC.doFinal(mensagem.getBytes("UTF-8"));
        return byte2Hex(bytesHMAC);
    }
    public static String hMac(String chave, Object obj) throws NoSuchAlgorithmException, IOException, InvalidKeyException {
        // preciso serializar o objeto
        Mac shaHMAC = Mac.getInstance(ALGORITMO);
        // serializando o objeto = convertendo para uma stream de bytes
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos);
        out.writeObject(obj);
        byte[] data = bos.toByteArray();
        SecretKeySpec chaveMAC = new SecretKeySpec(chave.getBytes("UTF-8"), ALGORITMO);
        shaHMAC.init(chaveMAC);
        byte[] bytesHMAC = shaHMAC.doFinal(data);
        return byte2Hex(bytesHMAC);
    }
    public static String byte2Hex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for(byte b: bytes)
            sb.append(String.format("%02x", b));
        return sb.toString();
    }
}