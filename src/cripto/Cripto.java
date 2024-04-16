package cripto;

import model.Mensagem;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;


public class Cripto implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// classe que engloba todas as etapas de encriptação do trabalho.
    public AES aes;
    public String chaveHmac;
    public Rsa rsa;
    public Cripto(String chaveAES, Chave chaveRsaPublica, Chave chaveRsaPrivada) throws UnsupportedEncodingException {
        this.aes = new AES();
        this.aes.reconstruirChave(chaveAES.getBytes());
        this.rsa = new Rsa();
        this.rsa.setPublicKey(chaveRsaPublica);
        this.rsa.setPrivateKey(chaveRsaPrivada);
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
    public Cripto(Cripto c){
        this.aes = c.aes;
        this.rsa = c.rsa;
        this.chaveHmac = c.chaveHmac;
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
    public String verificarAssinatura(DadoCifrado assinatura) throws UnsupportedEncodingException {
       return new String(rsa.decifrar(assinatura,rsa.getPublicKeyExterna()), "UTF-8");
    }
    public static void main(String[] args) throws UnsupportedEncodingException {

    }
}
