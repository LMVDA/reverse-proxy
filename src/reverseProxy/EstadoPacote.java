package reverseProxy;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class EstadoPacote {
    
    private int numeroSequenciaPacote;
    private LocalDateTime dataEnvio;

    public EstadoPacote(int numeroPacote) {
        this.numeroSequenciaPacote = numeroPacote;
        dataEnvio = LocalDateTime.now();
    }

    public int getNumeroSequenciaPacote() {
        return numeroSequenciaPacote;
    }

    public void setNumeroSequenciaPacote(int numeroSequenciaPacote) {
        this.numeroSequenciaPacote = numeroSequenciaPacote;
    }

    public LocalDateTime getDataEnvio() {
        return dataEnvio;
    }

    public void setDataEnvio(LocalDateTime dataEnvio) {
        this.dataEnvio = dataEnvio;
    }
    
    public long diffInMillis() {
        LocalDateTime agora = LocalDateTime.now();
        long diff = ChronoUnit.MILLIS.between(dataEnvio, agora);
        
        return diff;
    }
    
    public boolean isValid(long difTempoPermitida){
        return diffInMillis() < difTempoPermitida;
    }
    
    
}
