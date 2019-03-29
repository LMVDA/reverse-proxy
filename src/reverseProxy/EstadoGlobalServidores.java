package reverseProxy;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class EstadoGlobalServidores {
    
    private Map<String, EstadoServidor> estado; //Map IP-Estado (servidor backend)
    
    
    public EstadoGlobalServidores() {
        estado = new HashMap<>();
    }
    
    public synchronized String getServidor(){
        Optional<EstadoServidor> first = estado.values().stream().filter(e -> e.isDisponivel()).sorted((e1, e2) -> Float.compare(e1.getScore(), e2.getScore())).findFirst();
        
        if (first.isPresent()) return first.get().getIp();
        else return null; 
    } 

    public synchronized EstadoServidor getEstadoServidor(String ip){
        return estado.get(ip);
    }
   
    public synchronized void adicionarServidor(String ip, EstadoServidor estadoServidor){
        estado.put(ip, estadoServidor); //colocar informação na tabela
    }

}
