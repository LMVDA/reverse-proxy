package protocoloMonitorizicacao;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class SocketMonitorizacao {
    private DatagramSocket socket;
    
    public SocketMonitorizacao() throws SocketException {
        socket = new DatagramSocket();
    }
    
    public SocketMonitorizacao(int porta) throws SocketException {
        socket = new DatagramSocket(porta);
    }
    
    public PacoteMonitorizacao receber() throws IOException { //primitiva receber
        int tamPDU = PDU.tamanho();
        byte[] receiveData = new byte[tamPDU];
        DatagramPacket pacote = new DatagramPacket(receiveData, tamPDU);
        
        socket.receive(pacote);
        
        return new PacoteMonitorizacao(pacote);
    }
    
    public void enviar(PacoteMonitorizacao pacote) throws IOException { //primitiva enviar
        socket.send(pacote.getPacote());
    }
}
