package cripto;

import model.Mensagem;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;


public class Cripto {
    // classe que engloba todas as etapas de encriptação do trabalho.
    public AES aes;
    public String chaveHmac;
    public Rsa rsa;
    public Cripto(String chaveAES, String chaveHmac) throws UnsupportedEncodingException {
        this.aes = new AES();
        this.rsa = new Rsa();
        this.chaveHmac = chaveHmac;
    }
    public Cripto(AES aes){
        this.aes = aes;
        this.rsa = new Rsa();
    }
    public Cripto(String chaveHmac){
        this.chaveHmac = chaveHmac;
        this.aes = new AES();
        this.rsa = new Rsa();
    }
    public Cripto(){
        this.aes = new AES();
        this.rsa = new Rsa();
    }

    public byte[] criptografar(Mensagem msg) throws IOException {
        byte[] msgEncriptada = aes.cifrar(Mensagem.serializar(msg));
        msgEncriptada = Base64.codificar(msgEncriptada);

        return msgEncriptada;
    }
    public Mensagem descriptografar(byte[] msgCriptografada) throws IOException, ClassNotFoundException {
        byte[] msgDescriptografada = Base64.decodificar(msgCriptografada);
        msgDescriptografada = aes.decifrar(msgDescriptografada);

        return Mensagem.deserializar(msgDescriptografada);
    }
    public String hMac(Object obj) throws Exception {
        return HMAC.hMac(chaveHmac, obj);
    }
    public DadoCifrado assinarHash(String hmac){
        return rsa.cifrar(hmac.getBytes(), rsa.getPrivateKey());
    }
    public String verificarAssinatura(DadoCifrado assinatura){
       return new String(rsa.decifrar(assinatura,rsa.getPublicKeyExterna()));
    }
}
