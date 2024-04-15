package model;

import cripto.DadoCifrado;

import java.io.*;
import java.math.BigInteger;

public class Mensagem<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    DadoCifrado hMacAssinado;
    T mensagem;
    int port;
    public Mensagem(T mensagem, DadoCifrado hMacAssinado, int port){
        this.mensagem = mensagem;
        this.hMacAssinado = hMacAssinado;
        this.port = port;
    }
    public Mensagem(T mensagem, DadoCifrado hMacAssinado){
        this.mensagem = mensagem;
        this.hMacAssinado = hMacAssinado;
    }
    public Mensagem(T dado) {
        this.mensagem = dado;
    }

    public DadoCifrado gethMacAssinado() {
        return hMacAssinado;
    }

    public void sethMacAssinado(DadoCifrado hMac) {
        this.hMacAssinado = hMac;
    }

    public T getMensagem() {
        return mensagem;
    }

    public void setMensagem(T mensagem) {
        this.mensagem = mensagem;
    }

    public static byte[] serializar(Mensagem obj) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos);
        out.writeObject(obj);
        byte[] data = bos.toByteArray();
        bos.close();
        out.close();
        return data;
    }
    public static Mensagem deserializar(byte[] objBytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bis = new ByteArrayInputStream(objBytes);
        ObjectInputStream in = new ObjectInputStream(bis);
        Mensagem retorno = (Mensagem) in.readObject();
        bis.close();
        in.close();
        return retorno;
    }
}
