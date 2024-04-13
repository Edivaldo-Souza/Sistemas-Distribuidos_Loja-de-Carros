package cripto;

import java.math.BigInteger;
import java.security.SecureRandom;

public class Rsa {
    private Chave publicKey;
    private Chave privateKey;
    private Chave publicKeyExterna;
    public Rsa(){
        // sortear dois primos aleatórios bem grandes
        SecureRandom random = new SecureRandom();
        int bitLength = 8;
        BigInteger p = new BigInteger(bitLength, 100, random);
        BigInteger q = new BigInteger(bitLength, 100, random);
        while(p.compareTo(q) == 0){
            q = new BigInteger(bitLength,100,random);
        }
        BigInteger n = p.multiply(q);

        BigInteger phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
        BigInteger e = gerarChavePublica(phi);
        BigInteger d = gerarChavePrivada(e,phi);
        this.privateKey = new Chave(d, n); // a chave privada é {d,n}
        this.publicKey = new Chave(e, n); // a chave publica é {e,n}
    }
    public static BigInteger gerarChavePublica(BigInteger phi){
        BigInteger coprime = new BigInteger("1");
        do{
            coprime = coprime.add(BigInteger.ONE);
        } while(coprime.gcd(phi).compareTo(BigInteger.ONE) != 0); // compareTo retorna zero quando são iguais
        return coprime;
    }
    public BigInteger gerarChavePrivada(BigInteger e, BigInteger phi){
        BigInteger d = new BigInteger("1");
        while (true){
            if (e.multiply(d).mod(phi).compareTo(BigInteger.ONE) == 0){ // e * d % phi == 1
                break;
            }
            d = d.add(BigInteger.ONE);
        }
        return d;
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
}
