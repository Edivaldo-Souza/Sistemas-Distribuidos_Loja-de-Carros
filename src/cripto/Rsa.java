package cripto;

import model.Categorias;
import model.Mensagem;
import model.Veiculo;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.SecureRandom;

public class Rsa implements Serializable {
    private static final long serialVersionUID = 1L;

    private Chave publicKey;
    private Chave privateKey;
    private Chave publicKeyExterna;
    public Rsa(){
        // sortear dois primos aleatórios bem grandes
        SecureRandom random = new SecureRandom();
        int bitLength = 4;
        BigInteger p = BigInteger.probablePrime(bitLength,random);
        BigInteger q = BigInteger.probablePrime(bitLength,random);
        while(p.compareTo(q) == 0){
            q = BigInteger.probablePrime(bitLength,random);
        }
        BigInteger n = p.multiply(q);
        BigInteger phi = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));
        BigInteger e = BigInteger.TWO;
        while(phi.gcd(e).compareTo(BigInteger.ONE) != 0 && e.compareTo(phi)<0) {
            e = e.add(BigInteger.ONE);
        }
        BigInteger d = e.modInverse(phi);
        this.privateKey = new Chave(d, n); // a chave privada é {d,n}
        this.publicKey = new Chave(e, n); // a chave publica é {e,n}
    }

    public DadoCifrado cifrar(byte[] mensagem, Chave chave){
        DadoCifrado cifrado = new DadoCifrado(mensagem.length);
        int i = 0;
        for (byte b: mensagem){
            BigInteger temp = new BigInteger(String.valueOf((int)b));
            if(temp.compareTo(BigInteger.ZERO) < 0){
                cifrado.sinais[i] = false; // o valor é negativo
                temp = temp.abs();
            } else {
                cifrado.sinais[i] = true;
            }
            cifrado.valores[i] = temp.modPow(chave.valorDaChave,chave.modulo);
                i++;
        }
        return cifrado;
    }
    public byte[] decifrar(DadoCifrado mensagemCifrada, Chave chave){
        byte[] decifrado = new byte[mensagemCifrada.valores.length];
        int i = 0;
        for (BigInteger b: mensagemCifrada.valores) {
            decifrado[i] = (byte) b.modPow(chave.valorDaChave,chave.modulo).intValue();
            if(!mensagemCifrada.sinais[i]){
                // se o sinal for negativo
                decifrado[i] = (byte) (~decifrado[i] + 1);
            }
            i++;
        }
        return decifrado;
    }
    public Chave getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(Chave publicKey) {
        this.publicKey = publicKey;
    }

    public Chave getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(Chave privateKey) {
        this.privateKey = privateKey;
    }


    public Chave getPublicKeyExterna() {
        return publicKeyExterna;
    }

    public void setPublicKeyExterna(Chave publicKeyExterna) {
        this.publicKeyExterna = publicKeyExterna;
    }
    public static void main(String[] args) throws Exception {
        Cripto cripto = new Cripto();
        Cripto cripto2 = new Cripto("123456789jsadscj");
        cripto2.rsa.setPublicKeyExterna(cripto.rsa.getPublicKey());
        cripto.rsa.setPublicKeyExterna(cripto2.rsa.getPublicKey());
        cripto.chaveHmac = cripto2.chaveHmac;
        cripto.aes.reconstruirChave(cripto2.aes.chave.getEncoded());
        //        Veiculo text = new Veiculo(Categorias.ECONOMICO,"32","ksd",2022,9202.2);
        String text = "osajdad";

        byte[] dado = cripto2.criptografar(new Mensagem(text,cripto2.assinarHash(cripto2.hMac(text))));

        Mensagem desc = cripto.descriptografar(dado);

        System.out.println(desc.getMensagem());
        System.out.println(desc.gethMacAssinado());
        System.out.println(cripto2.hMac(text));
        System.out.println(cripto.verificarAssinatura(desc.gethMacAssinado()));



    }
}
