package model;

public class NaoAutenticoException extends Exception{
    private String mensagem;

    public String getMensagem() {
        return mensagem;
    }

    public NaoAutenticoException(String msg){
        mensagem = msg;
    }
}
