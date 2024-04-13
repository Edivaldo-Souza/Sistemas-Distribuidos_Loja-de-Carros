package cripto;

import model.Mensagem;

import java.io.*;
import java.math.BigInteger;

public class DadoCifrado implements Serializable  {
    private static final long serialVersionUID = 1L;
    boolean[] sinais; // 0 para negativo e 1 para positivo
    BigInteger[] valores;
    public DadoCifrado(int length){
        sinais = new boolean[length];
        valores = new BigInteger[length];
    }
    public static byte[] serializar(DadoCifrado obj) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos);
        out.writeObject(obj);
        byte[] data = bos.toByteArray();
        bos.close();
        out.close();
        return data;
    }
    public static DadoCifrado deserializar(byte[] objBytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bis = new ByteArrayInputStream(objBytes);
        ObjectInputStream in = new ObjectInputStream(bis);
        DadoCifrado retorno = (DadoCifrado) in.readObject();
        bis.close();
        in.close();
        return retorno;
    }
}
