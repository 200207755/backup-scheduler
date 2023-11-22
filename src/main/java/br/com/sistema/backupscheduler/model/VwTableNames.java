package br.com.sistema.backupscheduler.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="VW_TABLE_NAMES")
public class VwTableNames implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private String tabela;
				
	public VwTableNames() {
	}

	public String getTabela() {
		return tabela;
	}

	public void setTabela(String tabela) {
		this.tabela = tabela;
	}

}