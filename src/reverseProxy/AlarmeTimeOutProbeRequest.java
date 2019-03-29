package reverseProxy;

public class AlarmeTimeOutProbeRequest extends Thread{
    
    private EstadoServidor estado;
    private long tempoTimeOut;
    private int nrSequenciaPacote;
    
    public AlarmeTimeOutProbeRequest(EstadoServidor estado, long tempoTimeOut, int nrSequenciaPacote){
        this.estado = estado;
        this.tempoTimeOut = tempoTimeOut;
        this.nrSequenciaPacote = nrSequenciaPacote;
    }
    
    public void run(){
        try {
            Thread.sleep(tempoTimeOut);
            
            synchronized(estado) {
                if(estado.pacoteExiste(nrSequenciaPacote)){
                    estado.registaPerda(nrSequenciaPacote);
                }
            }
        } catch (InterruptedException ex) {
        }
    }
}

