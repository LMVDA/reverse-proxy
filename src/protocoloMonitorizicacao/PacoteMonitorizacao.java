package protocoloMonitorizicacao;

import java.net.DatagramPacket;
import java.net.InetAddress;

public class PacoteMonitorizacao {
    private DatagramPacket pacote;
    
    public PacoteMonitorizacao(PDU pdu, InetAddress ip, int porta) {
        if (pdu == null) throw new RuntimeException("O pacote não pode ser nulo");
        
        byte[] dadosAEnviar = pdu.getPDU();
        
        pacote = new DatagramPacket(dadosAEnviar, dadosAEnviar.length, ip, porta);
    }
    
    protected PacoteMonitorizacao(DatagramPacket pacote) {
        this.pacote = pacote;
    }
    
    public String getStringIp() {
        return pacote.getAddress().getHostAddress();
    }
    
    public InetAddress getIp() {
        return pacote.getAddress();
    }
    
    public int getPorta() {
        return pacote.getPort();
    }
    
    protected DatagramPacket getPacote() {
        return pacote;
    }
    
    public PDU getPDU() {
        byte[] dados = pacote.getData();
        PDU pdu = null;

        if (dados != null && dados.length > 0) {
            if (PDURequest.isPDUrequest(pacote.getData())) pdu = new PDURequest(dados);
            else if (PDUResponse.isPDUresponse(pacote.getData())) pdu = new PDUResponse(dados);
            else if (PDURegisto.isPDUregisto(pacote.getData())) pdu = new PDURegisto(dados);
        }
        
        return pdu;
    }
}
