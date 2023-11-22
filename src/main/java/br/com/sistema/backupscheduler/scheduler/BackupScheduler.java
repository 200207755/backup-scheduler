package br.com.sistema.backupscheduler.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import br.com.sistema.backupscheduler.service.MegaService;
import br.com.sistema.backupscheduler.service.RequisicaoDadosService;

@Component
public class BackupScheduler {

    @Autowired
    private RequisicaoDadosService requisicaoDadosService;
    @Autowired
    private MegaService megaService;
    
    @Scheduled(cron ="${scheduler.cron:*/5 * * * * ?}")
    public void run1() {
    	
    	System.out.println("\n\n\n\n\n\n*********************************** ENTROU NO SCHEDULER ************************\n\n\n\n\n\n\n");
    	requisicaoDadosService.realizarBackups();
    }
    
    @Scheduled(cron ="${scheduler.cron2:0 */1 * * * ?}")
    public void run2() {
    	System.out.println("\n\n\n\n\n\n*********************************** ENTROU NO SCHEDULER PARA EXCLUIR VENCIDOS ************************\n\n\n\n\n\n\n");
    	megaService.removerArquivosLinkVencido();
    	System.out.println("\n\n\n\n\n\n*********************************** TERMINOU EXCLUSAO VENCIDOS ************************\n\n\n\n\n\n\n");
    }
}
