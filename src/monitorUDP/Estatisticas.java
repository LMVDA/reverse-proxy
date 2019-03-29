/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monitorUDP;

import com.jezhumble.javasysmon.CpuTimes;
import com.jezhumble.javasysmon.JavaSysMon;
import com.jezhumble.javasysmon.MemoryStats;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author PL23
 */
public class Estatisticas {
     private JavaSysMon stats = new JavaSysMon();
     private CpuTimes cpuAnterior;

    public Estatisticas() {
        this.stats = new JavaSysMon();
        this.cpuAnterior = stats.cpuTimes();
    }
     
    public int calculaCPU() {
        int res;
        CpuTimes cpuAtual = stats.cpuTimes();
        res = (int) (cpuAtual.getCpuUsage(cpuAnterior) * 100);
        cpuAnterior = cpuAtual;
        return res;
    }
     
    public int calculaMemoria() {
        MemoryStats ram = stats.physical();
        return (int) (100 - (100.0 * ram.getFreeBytes())/ ram.getTotalBytes());
    }

    public int calculaConexoesTCP() {
        int res = 0;
        String[] cmd = {
            "/bin/sh",
            "-c",
            "netstat -nt | grep -i ESTABLISHED | wc -l"
            };
        try {
            Process p = Runtime.getRuntime().exec(cmd);
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String s = br.readLine();
            if(s != null) {
                res = Integer.parseInt(s.trim());
            }
            p.waitFor();
            p.destroy();
        } catch (IOException ex) {
            Logger.getLogger(Estatisticas.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
             Logger.getLogger(Estatisticas.class.getName()).log(Level.SEVERE, null, ex);
         }
        return res;
    }
}
