package monitorUDP;

import protocoloMonitorizicacao.PDURequest;
import protocoloMonitorizicacao.PDUResponse;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import protocoloMonitorizicacao.SocketMonitorizacao;
import protocoloMonitorizicacao.PacoteMonitorizacao;

public class MonitorUDP {
    
    private final InetAddress ipProxy;
    private final int portaProxy;
    private final static int portaMonitor = 5555;
    private EnviaRegistoPDU enviaRegistos;
    private Estatisticas stats;

    public MonitorUDP(InetAddress ip, int porta, int intervalo) throws UnknownHostException {
        this.ipProxy = ip;
        this.portaProxy = porta;
        this.enviaRegistos = new EnviaRegistoPDU(ipProxy, portaProxy, intervalo);
        this.stats = new Estatisticas();
    }

    public void start() {
        // iniciar o envio de mensagens de resgisto
        Thread tr = new Thread(this.enviaRegistos);
        tr.start();   
        
        try {
            // ficar à escuta de mensagens de probe request e enviar probe response
            SocketMonitorizacao socket = new SocketMonitorizacao(portaMonitor);
            PDUResponse response;
            int nrSequencia;
 
            while(true) {
                PacoteMonitorizacao pacote = socket.receber();
                
                // testa se o pacote recebido é um probe request
                if(pacote.getPDU() instanceof PDURequest) {
                    PDURequest pduRequest = (PDURequest) pacote.getPDU();
                    nrSequencia = pduRequest.getNrSequencia();
                    System.out.println("MONITOR: recebi PDU request nº "+nrSequencia+".");
                    response = new PDUResponse(nrSequencia);
                    
                    // calcular % RAM ocupada
                    response.setMemoria(stats.calculaMemoria());
                    // calcular % CPU
                    response.setCargaCPU(stats.calculaCPU());
                    // calcula lig. TCP
                    response.setNrConexoesTCP(stats.calculaConexoesTCP());
                    
                    pacote = new PacoteMonitorizacao(response, ipProxy, portaProxy);
                    socket.enviar(pacote);
                    System.out.println("MONITOR: enviei PDU response nº "+nrSequencia+".");
                }
                else {
                    System.out.println("Warning: PDU com código errado (tem que ser [10xx xxxx]");
                }
            }
        } catch (SocketException ex) {
        } catch (IOException ex) {
        }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws UnknownHostException {
        if(args.length < 2) {
            System.out.println("ERRO: necessita de um argumento com o intervalo (seg) entre mensagens de registo e o ip do reverse proxy.");
        }
        else {
            int intervaloMgsDisponivel = Integer.parseInt(args[0]);
           
            if(intervaloMgsDisponivel > 127 || intervaloMgsDisponivel < 1) {
                System.out.println("ERRO: o intervalo de tempo tem de ser entre 1 e 127!");
            }
            else {
                try{
                    InetAddress ip = InetAddress.getByName(args[1]);
                    MonitorUDP monitor = new MonitorUDP(ip, 5555, intervaloMgsDisponivel);
                    monitor.start();
                } catch (UnknownHostException ex) {
                    System.out.println("ERRO: ip inválido.");
                }
            }
            
        }
    }
    
}
