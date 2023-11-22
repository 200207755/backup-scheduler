package br.com.sistema.backupscheduler.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.sistema.backupscheduler.model.RequisicaoDados;
import br.com.sistema.backupscheduler.model.VwTableNames;
import br.com.sistema.backupscheduler.repository.RequisicaoDadosRepository;
import br.com.sistema.backupscheduler.repository.TableNamesRepository;
import br.com.sistema.backupscheduler.service.MegaService;
import br.com.sistema.backupscheduler.service.RequisicaoDadosService;

@Service
public class RequisicaoDadosServiceImpl implements RequisicaoDadosService {

	@Autowired
	private MegaService megaService;
	@Autowired
	private RequisicaoDadosRepository requisicaoDadosRepository;
	@Autowired
	private TableNamesRepository tableNamesRepository;
	
	private static final String PASTA_LOCAL = "C:\\tmp\\"; 
	public void realizarBackups() {
		File file =null;
		List<RequisicaoDados> listaRequisicoes = requisicaoDadosRepository.buscarRequisicoesEmAberto("BANCODEDADOS");
		for (RequisicaoDados requisicao : listaRequisicoes) {
			Date data = new Date();
			String banco = requisicao.getBancoDados();
			try {//try vai ser por requisição, caso gere qualquer erro finaliza a requisicao atual e segue para a próxima
				String caminhoBase=PASTA_LOCAL;
				criarPasta(caminhoBase);
				List<VwTableNames> tabelas = tableNamesRepository.buscarTabelas(banco);
				int cont = 0;
				List<String> arquivos = new ArrayList<String>();
				System.out.println("\n ****** INICIANDO GERAÇÃO XML PARA O TENANT ( "+banco+" ) *******");
				
				for (VwTableNames vw : tabelas) {
					cont ++;
					System.out.println("\n Criando SQL: ....... "+cont+" DE "+tabelas.size()+"......TABELA:"+vw.getTabela());
					String caminhoSqlInsert = caminhoBase+vw.getTabela()+".sql";
					escreverDatabaseTableInsert(banco, caminhoSqlInsert, vw.getTabela());
					if (caminhoSqlInsert != null && !arquivos.contains(caminhoSqlInsert)) {
						arquivos.add(caminhoSqlInsert);
					}
				}
				//GERA O ARQUIVO COM O CREATE DAS TABELAS
				String caminhoSqlCreate = caminhoBase+"databaseCreate.sql";
				escreverDatabaseTableCreate(banco, caminhoSqlCreate);
				if (caminhoSqlCreate != null && !arquivos.contains(caminhoSqlCreate)) {
					arquivos.add(caminhoSqlCreate);
				}
				
				String nomeArquivo = dataFormatada(data)+"_"+requisicao.getCodigo()+".zip";
				String caminhoArquivo = caminhoBase+nomeArquivo;
				zip(arquivos, caminhoArquivo);
				
				cont = 0;
				System.out.println("\n ****** INICIANDO EXCLUSAO DE ARQUIVOS TEMPORARIOS*******");
				for (String arquivo : arquivos) {
					cont ++;
					System.out.println("\n Excluindo arquivos: .........."+cont+" DE "+arquivos.size());
					file = new File(arquivo);
					if (file.exists()) {
						file.delete();
					}
				}
				requisicao.setArquivo(caminhoArquivo);
				requisicao.setDataProcessamento(data);
				requisicao.setStatus("PROCESSANDO");
			} catch (Exception e) {
				requisicao.setStatus("ERRO AO PROCESSAR");
				requisicao.setDataProcessamento(data);
				System.out.println(e);
			} finally {
				requisicaoDadosRepository.saveAndFlush(requisicao);
				System.out.println("\n ****** PROCESSAMENTO REQUISIÇÃO FINALIZADO *******");
			}
			if (!requisicao.getStatus().equals("ERRO AO PROCESSAR")) {
				System.out.println("\n ****** INCIANDO PROCESSO DE UPLOAD *******");
				megaService.realizarUploadMega(requisicao);
				System.out.println("\n ****** PROCESSAMENTO FINALIZADO *******");
			}
		}
		
	}
	
	private void escreverDatabaseTableCreate(String tenant, String caminho) throws Exception {
		File file = new File(caminho);
		file.createNewFile();
		FileWriter writer = new FileWriter(caminho);
		for (String linha : requisicaoDadosRepository.listarTableCreate(tenant)) {
			writer.write(linha+System.lineSeparator());
		}
		writer.close();
	}

	private void escreverDatabaseTableInsert(String tenant, String caminho, String tabela) throws Exception {
		File file = new File(caminho);
		if (!file.exists()) {
			file.createNewFile();
		}
		file = null;
		FileWriter writer = new FileWriter(caminho, true);
		String insertInto = requisicaoDadosRepository.buscarInicioInsert(tenant, tabela); 
		for (String value : requisicaoDadosRepository.listarTableInsertValues(tenant, tabela)) {
			writer.write(insertInto+" "+value+System.lineSeparator());
		}
		writer.close();
	}
	
	private void zip(List<String> files, String caminhoArquivo) throws IOException {
		System.out.println("\n ****** INICIANDO COMPACTACAO PARA ZIP *******");
		File outputFile = new File(caminhoArquivo);
		if (files != null && files.size() > 0) {
			FileOutputStream fileStream = new FileOutputStream(outputFile);
			ZipOutputStream out = new ZipOutputStream(fileStream);
			byte[] buf = new byte[1024];
			int cont = 0;
			for (String caminhoFile : files) {
				cont ++;
				System.out.println("\n Compactando arquivos: .........."+cont+" DE "+files.size());
				File file = new File(caminhoFile);
				if (file.exists()) {
					FileInputStream in = null;
					try {
						in = new FileInputStream(file);
						out.putNextEntry(new ZipEntry(file.getName()));
						int len;
						while ((len = in.read(buf)) > 0) {
							out.write(buf, 0, len);
						}
						out.closeEntry();
						in.close();
					} finally {
						if (in != null) {
							in.close();
						}
					}
				}
			}
			out.close();
			fileStream.close();
		}
	}
	private void criarPasta(String caminhoBase) {
		File folder = new File(caminhoBase+File.separator+PASTA_LOCAL);
		if (!folder.exists()) {
			folder.mkdir();
		}
	}
	private String dataFormatada(Date data) {
		DateFormat formatter = new SimpleDateFormat("dd_MM_yyyy");
		return formatter.format(data);
	}
}
