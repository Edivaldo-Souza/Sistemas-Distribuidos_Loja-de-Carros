package cripto;

import model.Mensagem;

import javax.crypto.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.spec.SecretKeySpec;
public class AES {
    public SecretKey chave;
    private KeyGenerator geradorDeChaves;
    public AES(SecretKey chave) throws UnsupportedEncodingException {
        this.chave = chave;
    }
    public AES(){
        gerarChave();
    }
    public void gerarChave() {
        try {
            geradorDeChaves = KeyGenerator
                    .getInstance("AES");
            chave = geradorDeChaves
                    .generateKey();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
    public void reconstruirChave(byte[] keyCifrada){
        chave = new SecretKeySpec(keyCifrada, "AES");
    }
        
    public byte[] decifrar(byte[] msgCriptografada){
        Cipher decriptador;
        byte[] msgDescriptada = new byte[msgCriptografada.length];
        try {
            decriptador = Cipher.getInstance("AES/ECB/PKCS5Padding");
            decriptador.init(Cipher.DECRYPT_MODE, chave);
            msgDescriptada = decriptador.doFinal(msgCriptografada);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException |
                 InvalidKeyException |IllegalBlockSizeException
                 | BadPaddingException e) {
            e.printStackTrace();
        }

        return msgDescriptada;
    }
    public byte[] cifrar(byte[] msg){
        Cipher cifrador;
        byte[] msgCriptografada = new byte[msg.length];
        try {
            cifrador = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cifrador.init(Cipher.ENCRYPT_MODE, chave);
            msgCriptografada = cifrador.doFinal(msg);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException |
                 InvalidKeyException |IllegalBlockSizeException
                 | BadPaddingException e) {
            e.printStackTrace();
        }
        return msgCriptografada;
    }
}
