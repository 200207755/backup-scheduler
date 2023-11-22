package br.com.sistema.backupscheduler.repository.custom;

import java.util.List;

import br.com.sistema.backupscheduler.model.VwTableNames;

public interface TableNamesCustomRepository {

	public List<VwTableNames> buscarTabelas(String banco);
	
}