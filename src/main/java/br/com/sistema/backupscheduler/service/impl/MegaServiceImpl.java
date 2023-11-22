package br.com.sistema.backupscheduler.service.impl;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.sistema.backupscheduler.constant.ExecCommand;
import br.com.sistema.backupscheduler.model.RequisicaoDados;
import br.com.sistema.backupscheduler.repository.RequisicaoDadosRepository;
import br.com.sistema.backupscheduler.service.MegaService;
import io.github.eliux.mega.Mega;
import io.github.eliux.mega.MegaServer;
import io.github.eliux.mega.MegaSession;
import io.github.eliux.mega.cmd.ExportInfo;
import io.github.eliux.mega.cmd.FileInfo;

@Service
public class MegaServiceImpl implements MegaService {
	@Autowired
	private RequisicaoDadosRepository requisicaoDadosRepository;
	
	private static final String PASTA_MEGA = "arquivos/";
	private static final Integer LIMITE_TENTATIVAS_UPLOAD = 3; 
	
	public void realizarUploadMega(RequisicaoDados req) {
		try {
			File file = new File(req.getArquivo());
			if (!file.exists()) {
				throw new Exception("Arquivo nao encontrado para realizar upload.");
			}
			
			ExportInfo info = null;
			int tentativas = 0;
	        while(tentativas < LIMITE_TENTATIVAS_UPLOAD) {
	        	System.out.println("Inciando Upload Arquivo REQ: "+req.getCodigo()+"  Tentativa "+(tentativas+1)+" de "+LIMITE_TENTATIVAS_UPLOAD);
	        	try {
					MegaServer.getCurrent().start();
					Thread.sleep(15000);
					MegaSession session = Mega.currentSession();
					
					String upload = req.getArquivo();
					String comando = null;
					if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
						comando = "cmd /c mega-put "+upload+" "+PASTA_MEGA;
					} else {
						comando = "mega-put "+upload+" "+PASTA_MEGA;	
					}
					new ExecCommand(comando); 
					
				    System.out.println("Iniciando criacao link publico para o arquivo");
				    LocalDate expira = LocalDate.now();
			        expira = expira.plusDays(15);
		        	info = session.export(PASTA_MEGA+req.getArquivo().substring(req.getArquivo().lastIndexOf(File.separator)+1, req.getArquivo().length())).setExpireDate(expira).enablePublicLink().call();   
		        	tentativas = LIMITE_TENTATIVAS_UPLOAD;//se chegar aqui é pq não deu erro e não precisa mais tentar fazer upload
	        	} catch (Exception e) {
	        		tentativas ++;
	        		if (tentativas >= LIMITE_TENTATIVAS_UPLOAD) {
	        			throw e;
	        		}
				}
	        }
	        System.out.println("Link criado");
			req.setLink(info.getPublicLink());
			req.setStatus("CONCLUÍDO COM SUCESSO");
		} catch (Exception e) {
			System.out.println("ERRO NO UPLOAD:"+e.getMessage());
			req.setLink("ERRO NO UPLOAD");
			req.setStatus("ERRO NO UPLOAD");
		} finally {
			//salva a requisição e apaga o arquivo local criado
			requisicaoDadosRepository.saveAndFlush(req);
			File file = new File(req.getArquivo());
			if (file.exists()) {
				file.delete();
			}
		}
	
	}
	
	/*private String getNomePasta(String nomePasta) {
    	nomePasta = nomePasta.replace(File.separator, "");
    	if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
    		nomePasta = nomePasta.replace("C:tmp", "");//replace pra pegar o nome da pasta, tem verificação do windows
    	} else {
    		nomePasta = nomePasta.replace("tmp", "");
    	}
    	return nomePasta;
	}
	*/
	/*public void excluirArquivo(String nomeArquivo, boolean caminhoDiretorio) throws Exception {
		
		MegaServer.getCurrent().start();
		Thread.sleep(10000);
		MegaSession session =  Mega.currentSession();
		if (caminhoDiretorio) {
			nomeArquivo = getNomePasta(nomeArquivo);
			session.removeDirectory(PASTA_MEGA+nomeArquivo).run();
		} else {
			session.remove(PASTA_MEGA+nomeArquivo).run();
		}
	}*/
	
	public void removerArquivosLinkVencido() {
		try {	
			LocalDateTime data = LocalDateTime.now().minusDays(30);
			MegaServer.getCurrent().start();
			Thread.sleep(10000);
			MegaSession session = Mega.currentSession();
			List<FileInfo> lista = session.ls(PASTA_MEGA).call();
			int count=0;
			for (FileInfo info : lista) {
				if (info.getDate().isBefore(data)) {
					System.out.println("EXCLUINDO REGISTRO : "+info.getName());
					if (info.isDirectory()) {
						session.removeDirectory(PASTA_MEGA+info.getName()).run();
					} else {
						session.remove(PASTA_MEGA+info.getName()).run();
					}
					count++;
				}
			}
			System.out.println("TOTAL REGISTROS EXCLUIDOS DO MEGA: "+ count);
		} catch (Exception e) {
			System.out.println("ERRO INESPERADO AO CONSULTAR ARQUIVOS NO MEGA: "+ e.getMessage());
		}
	}
}