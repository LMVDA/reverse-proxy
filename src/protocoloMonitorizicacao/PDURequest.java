package protocoloMonitorizicacao;

public class PDURequest extends PDU {

    private static final byte id = (byte) (1 << 7); // Byte -> 1000 0000
    
    private int nrSequencia;
    
    public PDURequest(byte[] dados) {
        nrSequencia = new Byte(dados[1]).intValue();
    }

    public PDURequest(int nrSequencia) {
        this.nrSequencia = nrSequencia;
    }

    public int getNrSequencia() {
        return nrSequencia;
    }
    
    public byte[] getPDU() {
        byte[] res = new byte[2];
        res[0] = id;
        res[1] = (byte) nrSequencia;
        return res;
    }
    
    public static boolean isPDUrequest(byte[] pdu) {
        return Byte.compare(id, pdu[0]) == 0;
    }
}