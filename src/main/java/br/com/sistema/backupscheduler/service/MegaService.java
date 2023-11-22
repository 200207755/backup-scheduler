package br.com.sistema.backupscheduler.service;

import br.com.sistema.backupscheduler.model.RequisicaoDados;

public interface MegaService {

	public void realizarUploadMega(RequisicaoDados req);
	public void removerArquivosLinkVencido();
}