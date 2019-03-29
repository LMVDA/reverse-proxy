package reverseProxy;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {
    
    
    public static void main(String[] args){
        
        int portaServidor = 5555;
        int portaMonitores = 5555;
        int timeOutProbeRequest = 500;
        long intervaloEntreEnviosProbeRequest = 5000;
        long tempoTimeOutServidorAtivo = 10000;
        int portaServidoresBackendTCP = 80;
        int portaServidorTCP = 80;
        
        Configuracoes configuracoes = new Configuracoes(portaServidor, portaMonitores, timeOutProbeRequest, intervaloEntreEnviosProbeRequest, tempoTimeOutServidorAtivo);
        
        EstadoGlobalServidores estadoGlobalServidores = new EstadoGlobalServidores();
        Receptor receptor = new Receptor(estadoGlobalServidores, configuracoes);
        receptor.start();
        try {
            ServerSocket ss = new ServerSocket(portaServidorTCP);

        
            while(true){
                Socket clienteTCP = ss.accept();
                TrataClienteTCP cliente = new TrataClienteTCP(clienteTCP, portaServidoresBackendTCP, estadoGlobalServidores);
                cliente.start();
            }
            
            
        } catch (IOException ex) {
        }
    }
}
