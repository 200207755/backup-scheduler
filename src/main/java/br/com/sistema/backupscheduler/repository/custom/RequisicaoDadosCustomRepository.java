package br.com.sistema.backupscheduler.repository.custom;

import java.util.List;

import br.com.sistema.backupscheduler.model.RequisicaoDados;

public interface RequisicaoDadosCustomRepository {

	public List<RequisicaoDados> buscarRequisicoesEmAberto(String banco);
	public List<String> listarTableCreate(String banco);
	public String buscarInicioInsert(String banco, String tabela);
	public List<String> listarTableInsertValues(String banco, String tabela);
}