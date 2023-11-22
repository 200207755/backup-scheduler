package br.com.sistema.backupscheduler.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.sistema.backupscheduler.model.VwTableNames;
import br.com.sistema.backupscheduler.repository.custom.TableNamesCustomRepository;

@Repository
public interface TableNamesRepository extends JpaRepository<VwTableNames, Long>, TableNamesCustomRepository {

}
