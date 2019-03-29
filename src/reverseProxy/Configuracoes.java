package reverseProxy;

public class Configuracoes {
    
    private int portaServidor;
    private int portaMonitores;
    private long timeoutProbeRequest;
    private long intervaloEntreEnviosProbeRequest;
    private long tempoTimeOutServidorAtivo;

    public Configuracoes(int portaServidor, int portaMonitores, long timeoutProbeRequest, long intervaloEntreEnviosProbeRequest, long tempoTimeOutServidorAtivo) {
        this.portaServidor = portaServidor;
        this.portaMonitores = portaMonitores;
        this.timeoutProbeRequest = timeoutProbeRequest;
        this.intervaloEntreEnviosProbeRequest = intervaloEntreEnviosProbeRequest;
        this.tempoTimeOutServidorAtivo = tempoTimeOutServidorAtivo;
    }

    public int getPortaServidor() {
        return portaServidor;
    }

    public void setPortaServidor(int portaServidor) {
        this.portaServidor = portaServidor;
    }

    public int getPortaMonitores() {
        return portaMonitores;
    }

    public void setPortaMonitores(int portaMonitores) {
        this.portaMonitores = portaMonitores;
    }

    public long getTimeoutProbeRequest() {
        return timeoutProbeRequest;
    }

    public void setTimeoutProbeRequest(long timeoutProbeRequest) {
        this.timeoutProbeRequest = timeoutProbeRequest;
    }

    public long getIntervaloEntreEnviosProbeRequest() {
        return intervaloEntreEnviosProbeRequest;
    }

    public void setIntervaloEntreEnviosProbeRequest(long intervaloEntreEnviosProbeRequest) {
        this.intervaloEntreEnviosProbeRequest = intervaloEntreEnviosProbeRequest;
    }
    
    public long getTempoTimeOutServidorAtivo() {
        return tempoTimeOutServidorAtivo;
    }

    public void setTempoTimeOutServidorAtivo(long tempoTimeOutServidorAtivo) {
        this.tempoTimeOutServidorAtivo = tempoTimeOutServidorAtivo;
    }
}
