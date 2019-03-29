package reverseProxy;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import protocoloMonitorizicacao.PDURegisto;
import protocoloMonitorizicacao.PDUResponse;
import java.util.HashMap;
import java.util.Map;

public class EstadoServidor {
    private long tempoTimeOutServidorAtivo;
    private LocalDateTime dataUltimoPacoteRecebido;
    
    private long timeoutProbeRequest; // tempo maximo permitido para os pacotes probe request darem timeout
    
    private String ip;                 //IP do servidor back-end a que os seguintes dados se referem
    private int intervaloTempoRegisto; //de x e x segundos envia um registo "estou disponivel"
    
    private int nrConexoesTCP;
    private int sampleRTT;
    private int estimatedRTT;
    private int devRTT;
    private float alfa = 0.125f;
    private float beta = 0.25f;

    private int percentagemCargaCPU;
    private int percentagemCargaMemoria;
    
    private int nrPacotesEnviados;
    private int nrPacotesPerdidos;
    private Map<Integer, EstadoPacote> pacotesEnviadosPorConfirmar; //Map numero seq de pacote - pacote emviados
    
    private int nrProximoPacote;
    
    private boolean disponivel;
    
    public EstadoServidor(String ip, int intervaloTempoRegisto, long timeoutProbeRequest, long tempoTimeOutServidorAtivo){
        this.ip = ip;
        pacotesEnviadosPorConfirmar = new HashMap<>();
        
        nrProximoPacote = 0;
        
        disponivel = true;
        
        this.intervaloTempoRegisto = intervaloTempoRegisto;
        this.tempoTimeOutServidorAtivo = tempoTimeOutServidorAtivo;
        this.timeoutProbeRequest = timeoutProbeRequest;
        
        sampleRTT = 0;
        estimatedRTT = 200;
        devRTT = 0;
        
        dataUltimoPacoteRecebido = LocalDateTime.now();
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public synchronized int getIntervaloTempoRegisto() {
        return intervaloTempoRegisto;
    }

    public void setIntervaloTempoRegisto(int intervaloTempoRegisto) {
        this.intervaloTempoRegisto = intervaloTempoRegisto;
    }

    public int getNrConexoesTCP() {
        return nrConexoesTCP;
    }

    public void setNrConexoesTCP(int nrConexoesTCP) {
        this.nrConexoesTCP = nrConexoesTCP;
    }

    public int getSampleRTT() {
        return sampleRTT;
    }

    public void setSampleRTT(int sampleRTT) {
        this.sampleRTT = sampleRTT;
    }

    public int getPercentagemCargaCPU() {
        return percentagemCargaCPU;
    }

    public void setPercentagemCargaCPU(int percentagemCargaCPU) {
        this.percentagemCargaCPU = percentagemCargaCPU;
    }

    public int getPercentagemCargaMemoria() {
        return percentagemCargaMemoria;
    }

    public void setPercentagemCargaMemoria(int percentagemCargaMemoria) {
        this.percentagemCargaMemoria = percentagemCargaMemoria;
    }

    public int getNrPacotesEnviados() {
        return nrPacotesEnviados;
    }

    public void setNrPacotesEnviados(int nrPacotesEnviados) {
        this.nrPacotesEnviados = nrPacotesEnviados;
    }

    public int getNrPacotesPerdidos() {
        return nrPacotesPerdidos;
    }

    public void setNrPacotesPerdidos(int nrPacotesPerdidos) {
        this.nrPacotesPerdidos = nrPacotesPerdidos;
    }

    public Map<Integer, EstadoPacote> getPacotesEnviadosPorConfirmar() {
        return pacotesEnviadosPorConfirmar;
    }

    public void setPacotesEnviadosPorConfirmar(Map<Integer, EstadoPacote> pacotesEnviadosPorConfirmar) {
        this.pacotesEnviadosPorConfirmar = pacotesEnviadosPorConfirmar;
    }

    public synchronized boolean isDisponivel() { //testa se o servidor backend ainda está disponivel
        return disponivel;
    }

    public synchronized void setDisponivel(boolean disponivel) {
        this.disponivel = disponivel;
    }
    
    //Registar um registo
    public synchronized void registarRegisto(PDURegisto pacoteRecebido){ 
        intervaloTempoRegisto = pacoteRecebido.getIntervaloMsgDisponivel(); //atualiza o x (de x em x tempo)
        
        disponivel = true;
        dataUltimoPacoteRecebido = LocalDateTime.now();
    }
    
    //registar um probe request enviado
    //devolve o numero do pacote
    public synchronized int registarProbeRequest(){
        EstadoPacote novoPacote = new EstadoPacote(nrProximoPacote); 
        pacotesEnviadosPorConfirmar.put(nrProximoPacote, novoPacote); //colocar na lista p/confirmar (esperamos um probe response)
        nrProximoPacote++;
        nrPacotesEnviados++;
        
        return novoPacote.getNumeroSequenciaPacote();
    }
    
    //registar um probe response recebido
    public synchronized void registarProbeResponse(PDUResponse pacoteRecebido){
        
        EstadoPacote pacoteEnviado = pacotesEnviadosPorConfirmar.get(pacoteRecebido.getNrSequencia()); 
        
        if (pacoteEnviado != null) { //pacote ainda está por confirmar
            atualizarDados(pacoteEnviado, pacoteRecebido);
            pacotesEnviadosPorConfirmar.remove(pacoteRecebido.getNrSequencia());
            dataUltimoPacoteRecebido = LocalDateTime.now();
        }
    }
    
    private synchronized void atualizarDados(EstadoPacote pacoteEnviado, PDUResponse pacoteRecebido){
        nrConexoesTCP = pacoteRecebido.getNrConexoesTCP();

        percentagemCargaCPU = pacoteRecebido.getCargaCPU();
        percentagemCargaMemoria = pacoteRecebido.getMemoria();
        
        sampleRTT = (int) pacoteEnviado.diffInMillis();
        estimatedRTT = (int) ((1-alfa)*estimatedRTT+alfa*sampleRTT);
        devRTT = (int) ((1-beta) * devRTT + Math.abs(sampleRTT-estimatedRTT));
        
        System.out.println(":: [" + ip + "] RTT Real:" + sampleRTT + " - Estimado: " + estimatedRTT);
    }
    
    //avalia se um pacote ainda existe na lista de pacotes por confirmar
    public synchronized boolean pacoteExiste(int nrSequenciaPacote){
        return pacotesEnviadosPorConfirmar.containsKey(nrSequenciaPacote);
    }
    
    //quando dá time out, regita perda
    public synchronized void registaPerda(int nrSequenciaPacote){
        pacotesEnviadosPorConfirmar.remove(nrSequenciaPacote);
        nrPacotesPerdidos++;
    }
    
    public synchronized void registaAtividade() {
        LocalDateTime agora = LocalDateTime.now();
        long diff = ChronoUnit.MILLIS.between(dataUltimoPacoteRecebido, agora);
        
        if (diff >= tempoTimeOutServidorAtivo) {
            if (disponivel) System.out.println(":: [" + ip + "] Servidor de back-end indisponivel");
            disponivel = false;
        }
        else {
            if (!disponivel) System.out.println(":: [" + ip + "] Servidor de back-end disponivel");
            disponivel = true;
        }
    }
    
    public synchronized float getScore(){
        float percentagemPerdas = (1.0f * nrPacotesPerdidos)/nrPacotesEnviados;
        
        if (percentagemCargaCPU >= 0.7 ||
            percentagemCargaMemoria >= 0.7 ||
            percentagemPerdas >= 0.7) {
            
            return 100;
        }
        
        float rttIdeal = 20;
        float nrConexoesTCPIdeal = 10;
        float percentagemPerdasIdeal = 0.01f;
        float percentagemCargaCPUIdeal = 10f;
        float percentagemCargaMemoriaIdeal = 10f;
        
        return  0.3f * (sampleRTT/rttIdeal) +
                0.3f * (nrConexoesTCP/nrConexoesTCPIdeal) +
                0.3f * (percentagemPerdas/percentagemPerdasIdeal) +
                0.05f * (percentagemCargaCPU/percentagemCargaCPUIdeal) +
                0.05f * (percentagemCargaMemoria/percentagemCargaMemoriaIdeal);
    }
}
