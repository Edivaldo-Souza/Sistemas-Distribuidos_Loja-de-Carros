package cripto;

import java.io.Serializable;
import java.math.BigInteger;

public class Chave implements Serializable {
    private static final long serialVersionUID = 1L;
    public BigInteger valorDaChave;
    public BigInteger modulo;

    public Chave(){

    }
    public Chave(BigInteger valorDaChave, BigInteger modulo){
        this.modulo = modulo;
        this.valorDaChave = valorDaChave;
    }
}
