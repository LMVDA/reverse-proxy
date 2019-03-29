package protocoloMonitorizicacao;

public class PDURegisto extends PDU {
    private final static byte id = (byte) 0;
    private int intervaloMsgDisponivel;
    
    public PDURegisto(byte[] dados) {
        intervaloMsgDisponivel = dados[1];
    }

    public PDURegisto(int intervaloMsgDisponivel) {
        this.intervaloMsgDisponivel = intervaloMsgDisponivel;
    }

    public int getIntervaloMsgDisponivel() {
        return intervaloMsgDisponivel;
    }

    public void setIntervaloMsgDisponivel(byte intervaloMsgDisponivel) {
        this.intervaloMsgDisponivel = intervaloMsgDisponivel;
    }
    
    public byte[] getPDU() {
        byte[] res = new byte[2];
        res[0] = PDURegisto.id;
        res[1] = (byte) intervaloMsgDisponivel;
        return res;
    }
    
    public static boolean isPDUregisto(byte[] pdu) {
        return Byte.compare(id, pdu[0]) == 0;
    }
    
}
