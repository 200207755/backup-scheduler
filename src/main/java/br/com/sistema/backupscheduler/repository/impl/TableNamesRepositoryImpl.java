package br.com.sistema.backupscheduler.repository.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import br.com.sistema.backupscheduler.model.VwTableNames;
import br.com.sistema.backupscheduler.repository.custom.TableNamesCustomRepository;

public class TableNamesRepositoryImpl implements TableNamesCustomRepository {

	@PersistenceContext(unitName="entityManager")
	private EntityManager entityManager;

	public List<VwTableNames> buscarTabelas(String banco) {
		Query query = entityManager.createNativeQuery("SELECT * FROM "+banco+"..VW_TABLE_NAMES ORDER BY TABELA ASC", VwTableNames.class);
		return query.getResultList();
	}
	

}
