package reverseProxy;

public class AlarmeAgenteAtivo extends Thread{
    
    private EstadoServidor estado;
    private long tempoTimeOutServidorAtivo;
    
    public AlarmeAgenteAtivo(EstadoServidor estado, long tempoTimeOutServidorAtivo){
        this.estado = estado;
        this.tempoTimeOutServidorAtivo = tempoTimeOutServidorAtivo;
    }
    
    public void run(){
        try {
            while (estado.isDisponivel()) {
                Thread.sleep(tempoTimeOutServidorAtivo);
            
                synchronized(estado) {
                    estado.registaAtividade();
                }
            }
        } catch (InterruptedException ex) {
        }
    }
}

