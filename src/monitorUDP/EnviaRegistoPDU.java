package monitorUDP;

import protocoloMonitorizicacao.PDURegisto;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import protocoloMonitorizicacao.SocketMonitorizacao;
import protocoloMonitorizicacao.PacoteMonitorizacao;

public class EnviaRegistoPDU implements Runnable {
    
    private InetAddress ipProxy;
    private int portaProxy;
    private PDURegisto reg;

    public EnviaRegistoPDU(InetAddress ipProxy, int portaProxy, int intervalo) {
        this.ipProxy = ipProxy;
        this.portaProxy = portaProxy;
        this.reg = new PDURegisto(intervalo);
    }

    
    public void run() {
        try {
            PacoteMonitorizacao pacote;
            SocketMonitorizacao clientSocket = new SocketMonitorizacao();
            while(true) {
                pacote = new PacoteMonitorizacao(reg, ipProxy, portaProxy);
                clientSocket.enviar(pacote);
                System.out.println("MONITOR: enviei PDU de registo.");
                Thread.sleep(reg.getIntervaloMsgDisponivel()*1000);
            }
        } catch (InterruptedException ex) {
        } catch (SocketException ex) {
        } catch (IOException ex) {
        }
    }
}
