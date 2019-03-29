package reverseProxy;

import protocoloMonitorizicacao.PDURequest;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import protocoloMonitorizicacao.PacoteMonitorizacao;
import protocoloMonitorizicacao.SocketMonitorizacao;

public class TimerProbeRequest extends Thread {
    private String ip;
    private EstadoServidor estado;
    private Configuracoes configuracoes;
    
    public TimerProbeRequest(String ip, EstadoServidor estado, Configuracoes configuracoes) {
        this.ip = ip;
        this.estado = estado;
        this.configuracoes = configuracoes;
    }
    
    public void run() {
        try {
            boolean deveCorrer = true; // deve continuar a enviar probe requests enquanto o servidor estiver ativo
            
            while(deveCorrer) {
                Thread.sleep(configuracoes.getIntervaloEntreEnviosProbeRequest()); //espera x mseg e acorda para enviar probe request 
                
                synchronized(estado) {
                    // se o servidor backend ainda se encontra disponível
                    if (estado.isDisponivel()) {
                        enviaProbeRequest();
                    }
                    else {
                        deveCorrer = false;
                    }
                }
            }
        } catch (InterruptedException ex) {
        } catch (UnknownHostException ex) {
        } catch (SocketException ex) {
        } catch (IOException ex) {
        }
    }
    
    private void enviaProbeRequest() throws UnknownHostException, SocketException, IOException {
        int nrSequencia = estado.registarProbeRequest(); //vai buscar o nr de seq
        PDURequest request = new PDURequest(nrSequencia); //criar um probe request
        InetAddress address = InetAddress.getByName(ip);//transformar a string ip num objecto ip
        PacoteMonitorizacao pacote = new PacoteMonitorizacao(request, address, configuracoes.getPortaMonitores()); // pacote para enviar
        
        SocketMonitorizacao socket = new SocketMonitorizacao(); //para enviar
        System.out.println("<< [" + ip + "] PDU Probe request - Nr Seq: " + nrSequencia);
        socket.enviar(pacote);
        
        AlarmeTimeOutProbeRequest alarmeTimeOutProbeRequest = new AlarmeTimeOutProbeRequest(estado, configuracoes.getTimeoutProbeRequest(), nrSequencia);
        alarmeTimeOutProbeRequest.start();
    }
}
