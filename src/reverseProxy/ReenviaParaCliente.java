package reverseProxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ReenviaParaCliente extends Thread {
    private OutputStream escreverParaCliente;
    private InputStream lerDoServidorBackEnd;

    public ReenviaParaCliente(OutputStream escreverParaCliente, InputStream lerDoServidorBackEnd) {
        this.escreverParaCliente = escreverParaCliente;
        this.lerDoServidorBackEnd = lerDoServidorBackEnd;
    }

    @Override
    public void run() {
        try {
            int bytesLidos;
            byte[] dadosResposta = new byte[4048];
            
            while ((bytesLidos = lerDoServidorBackEnd.read(dadosResposta)) != -1) {
                escreverParaCliente.write(dadosResposta, 0, bytesLidos);
                escreverParaCliente.flush();
            }
        } catch (IOException e) {
        }
        finally {
            try {
                escreverParaCliente.close();
            } catch (IOException e) { }
        }
    }
}
