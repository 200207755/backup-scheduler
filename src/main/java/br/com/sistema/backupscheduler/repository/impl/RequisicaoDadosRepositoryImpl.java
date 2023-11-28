package br.com.sistema.backupscheduler.repository.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import br.com.sistema.backupscheduler.model.RequisicaoDados;
import br.com.sistema.backupscheduler.repository.custom.RequisicaoDadosCustomRepository;

public class RequisicaoDadosRepositoryImpl implements RequisicaoDadosCustomRepository {

	@PersistenceContext(unitName="entityManager")
	private EntityManager entityManager;

	public List<RequisicaoDados> buscarRequisicoesEmAberto(String banco) {
		Query query = entityManager.createNativeQuery("SELECT * FROM "+banco+"..REQUISICAODADOS WHERE DATA_PROCESSAMENTO IS NULL AND "
				+ "STATUS='AGUARDANDO PROCESSAMENTO' ORDER BY CODIGO ASC;", RequisicaoDados.class);
		return query.getResultList();
	}
	
	public List<String> listarTableCreate(String banco) {
		Query query = entityManager.createNativeQuery("SELECT RESULT FROM "+banco+"..VW_TABLE_CREATE");
		return query.getResultList();
	}
	
	public String buscarInicioInsert(String banco, String tabela) {
		boolean usuarios = tabela.equals("USUARIO");//remove a coluna de senha da tabela de usuario
		return (String)entityManager.createNativeQuery("EXEC "+banco+"..SP_GERAR_INICIO_INSERT @tabela_nome='"+tabela+"'"+
					(usuarios?", @colunas_ignorar = \"'SENHA'\"":"")).getSingleResult();
	}
	
	public List<String> listarTableInsertValues(String banco, String tabela) {
		boolean usuarios = tabela.equals("USUARIO");//remove a coluna de senha da tabela de usuario
		String sql = (String)entityManager.createNativeQuery("EXEC "+banco+"..SP_GERAR_INSERT_VALUES @nome_tabela='"+tabela+"'"+
					(usuarios?", @colunas_ignorar = \"'SENHA'\"":"")).getSingleResult();
		int index = sql.indexOf("FROM");
		sql = (sql.substring(0, index+5) + banco+".."+sql.substring(index+5));
		Query query = entityManager.createNativeQuery(sql);
		return query.getResultList();
	}
}
