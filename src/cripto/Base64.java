package cripto;

public class Base64 {
    public static byte[] codificar(byte[] objBytes){
        return java.util.Base64
                .getEncoder()
                .encode(objBytes);
    }
    public static byte[] decodificar(byte[] bytesEncriptados){
        return java.util.Base64
                .getDecoder()
                .decode(bytesEncriptados);
    }
}
