package br.com.sistema.backupscheduler.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.sistema.backupscheduler.model.RequisicaoDados;
import br.com.sistema.backupscheduler.repository.custom.RequisicaoDadosCustomRepository;

@Repository
public interface RequisicaoDadosRepository extends JpaRepository<RequisicaoDados, Long>, RequisicaoDadosCustomRepository {

}
