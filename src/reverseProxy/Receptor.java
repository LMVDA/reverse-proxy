package reverseProxy;

import protocoloMonitorizicacao.PDURegisto;
import protocoloMonitorizicacao.PDUResponse;
import java.io.IOException;
import java.net.SocketException;
import protocoloMonitorizicacao.PacoteMonitorizacao;
import protocoloMonitorizicacao.SocketMonitorizacao;

public class Receptor extends Thread{
    
    private EstadoGlobalServidores estado;
    private Configuracoes configuracoes;
    
    public Receptor(EstadoGlobalServidores estado, Configuracoes configuracoes){
        this.estado = estado;
        this.configuracoes = configuracoes; 
    }
    
    public void run(){
        SocketMonitorizacao socket;
        PacoteMonitorizacao pacote;
        String ip;
            
        try {
            socket = new SocketMonitorizacao(configuracoes.getPortaServidor());
 
            while(true) {
                pacote = socket.receber(); //o socket preenche o pacote 
                ip = pacote.getStringIp(); //ip de quem enviou o pacote
                
                if (pacote.getPDU() instanceof PDURegisto) { //testa se é registo
                    System.out.println(">> [" + ip + "] PDU Registo");
                    PDURegisto pduRegisto = (PDURegisto) pacote.getPDU();
                    
                    synchronized(estado) {
                        EstadoServidor estadoServidor = estado.getEstadoServidor(pacote.getStringIp());

                        // se este servidor nao estava registado, é necessario registar
                        if (estadoServidor == null || !estadoServidor.isDisponivel()) {
                            estadoServidor = new EstadoServidor(ip, pduRegisto.getIntervaloMsgDisponivel(), configuracoes.getTimeoutProbeRequest(), configuracoes.getTempoTimeOutServidorAtivo());
                            estado.adicionarServidor(ip, estadoServidor); //colocar informação na tabela

                            // enviar periodicamente probe request
                            TimerProbeRequest timerProbeRequest = new TimerProbeRequest(ip, estadoServidor, configuracoes);
                            timerProbeRequest.start();
                            AlarmeAgenteAtivo alarmeAgenteAtivo = new AlarmeAgenteAtivo(estadoServidor, configuracoes.getTempoTimeOutServidorAtivo());
                            alarmeAgenteAtivo.start();
                        }
                        else {
                            estadoServidor.registarRegisto(pduRegisto);
                        }
                    }
                }
                else if (pacote.getPDU() instanceof PDUResponse) { //testa se o pacote é probe response
                    synchronized(estado) {
                        EstadoServidor estadoServidor = estado.getEstadoServidor(ip); //obter o estado do servidor backend

                        if (estadoServidor != null) { //se existir 

                            PDUResponse pduResponse = (PDUResponse) pacote.getPDU(); 
                            estadoServidor.registarProbeResponse(pduResponse);

                            System.out.println(">> [" + ip + "] PDU Probe response - Nr Seq: " + pduResponse.getNrSequencia());
                        }
                    }
                }
            }
        } catch (SocketException ex) {
        } catch (IOException ex) {
        }
    }
    
}
