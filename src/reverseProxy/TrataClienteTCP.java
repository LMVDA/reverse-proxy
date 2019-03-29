package reverseProxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class TrataClienteTCP extends Thread {
    
    private Socket cliente;
    private int porta;
    private EstadoGlobalServidores estado;
    
    public TrataClienteTCP(Socket cliente, int porta, EstadoGlobalServidores estado) {
        this.cliente = cliente;
        this.porta = porta;
        this.estado = estado;
    }

    @Override
    public void run() {
        InputStream lerDoCliente = null;
        byte[] dadosPedido = new byte[1024];
        Socket servidorBackEnd = null;
        
        try {
            lerDoCliente = cliente.getInputStream();
            OutputStream escreverParaCliente = cliente.getOutputStream();
            
            String ipServidor = estado.getServidor();
            
            if (ipServidor != null) { // se houver servidor backend para tratar

                servidorBackEnd = new Socket(ipServidor, porta);
            
                InputStream lerDoServidorBackEnd = servidorBackEnd.getInputStream();
                OutputStream escreverParaServidorBackEnd = servidorBackEnd.getOutputStream();

                ReenviaParaCliente reenviaParaCliente = new ReenviaParaCliente(escreverParaCliente, lerDoServidorBackEnd);
                reenviaParaCliente.start();

                int bytesLidos;

                while ((bytesLidos = lerDoCliente.read(dadosPedido)) != -1) {
                  escreverParaServidorBackEnd.write(dadosPedido, 0, bytesLidos);
                  escreverParaServidorBackEnd.flush();
                }
            }
        } catch (IOException ex) {
        } finally {
            try {
                if (cliente != null) cliente.close();
                if (servidorBackEnd != null) servidorBackEnd.close();
            } catch (IOException ex) { }
        }
    }
}
