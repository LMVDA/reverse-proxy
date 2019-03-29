package protocoloMonitorizicacao;

public abstract class PDU {
    public abstract byte[] getPDU();
    
    public static int tamanho() {
        return 15;
    }
}
