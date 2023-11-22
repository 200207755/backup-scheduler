package br.com.sistema.backupscheduler.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="REQUISICAODADOS")
/*@NamedStoredProcedureQueries({
	@NamedStoredProcedureQuery(
		name = "sp_export_xml",
		procedureName = "SP_EXPORT_XML",
		parameters = {
			@StoredProcedureParameter(name="@base", type=String.class, mode=ParameterMode.IN),
			@StoredProcedureParameter(name="@sql", type=String.class, mode=ParameterMode.IN)
		})
	})*/
public class RequisicaoDados implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long codigo;

	@Column(name="banco_dados")
	private String bancoDados;
	
	@Column(name="data_requisicao")
	private Date dataRequisicao;
	
	@Column(name = "data_processamento")
	private Date dataProcessamento;
	
	private String link;
	
	private String status;
	
	@Transient
	private String arquivo;

	public RequisicaoDados() {
	}
	public Long getCodigo() {
		return codigo;
	}
	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}
	public String getBancoDados() {
		return bancoDados;
	}
	public void setBancoDados(String bancoDados) {
		this.bancoDados = bancoDados;
	}
	public Date getDataRequisicao() {
		return dataRequisicao;
	}
	public void setDataRequisicao(Date dataRequisicao) {
		this.dataRequisicao = dataRequisicao;
	}
	public Date getDataProcessamento() {
		return dataProcessamento;
	}
	public void setDataProcessamento(Date dataProcessamento) {
		this.dataProcessamento = dataProcessamento;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getArquivo() {
		return arquivo;
	}
	public void setArquivo(String arquivo) {
		this.arquivo = arquivo;
	}
}