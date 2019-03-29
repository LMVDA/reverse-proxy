package protocoloMonitorizicacao;

import java.nio.ByteBuffer;

public class PDUResponse extends PDU {
    
    private final static byte id = (byte) ((1 << 7) | (1 << 6)); // Byte -> 1100 0000
    private int nrSequencia;
    private int nrConexoesTCP;
    private int cargaCPU;
    private int memoria;
    
    public PDUResponse(byte[] dados) {
        nrSequencia = dados[1];
        ByteBuffer bb = ByteBuffer.allocate(2);
        bb.put(0, dados[2]);
        bb.put(1, dados[3]);
        nrConexoesTCP = bb.getShort();
        cargaCPU = dados[4];
        memoria = dados[5];
    }

    public PDUResponse(int nrSequencia) {
        this.nrSequencia = nrSequencia;
        this.nrConexoesTCP = 0;
        this.cargaCPU = 0;
        this.memoria = 0;
    }

    public PDUResponse(int nrSequencia, int nrConexoesTCP, int cargaCPU, int memoria) {
        this.nrSequencia = nrSequencia;
        this.nrConexoesTCP = nrConexoesTCP;
        this.cargaCPU = cargaCPU;
        this.memoria = memoria;
    }

    public Byte getId() {
        return id;
    }

    public int getNrSequencia() {
        return nrSequencia;
    }

    public void setNrSequencia(int nrSequencia) {
        this.nrSequencia = nrSequencia;
    }

    public int getNrConexoesTCP() {
        return nrConexoesTCP;
    }

    public void setNrConexoesTCP(int nrConexoesTCP) {
        this.nrConexoesTCP = nrConexoesTCP;
    }

    public int getCargaCPU() {
        return cargaCPU;
    }

    public void setCargaCPU(int cargaCPU) {
        this.cargaCPU = cargaCPU;
    }

    public int getMemoria() {
        return memoria;
    }

    public void setMemoria(int memoria) {
        this.memoria = memoria;
    }
    
    public byte[] getPDU() {
        byte[] res = new byte[6];
        res[0] = id;
        res[1] = (byte) nrSequencia;
        byte[] tcp = ByteBuffer.allocate(2).putShort((short)nrConexoesTCP).array();
        res[2] = tcp[0];
        res[3] = tcp[1];
        res[4] = (byte) cargaCPU;
        res[5] = (byte) memoria;
        return res;
    }
    
    public static boolean isPDUresponse(byte[] pdu) {
        return Byte.compare(id, pdu[0]) == 0;
    }
    
}
