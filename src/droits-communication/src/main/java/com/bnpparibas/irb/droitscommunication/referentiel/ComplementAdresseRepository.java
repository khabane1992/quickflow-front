package com.bnpparibas.irb.droitscommunication.referentiel;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ComplementAdresseRepository extends JpaRepository<ComplementAdresse, Long> {

    List<ComplementAdresse> findByActifTrueOrderByLibelleAsc();

    Optional<ComplementAdresse> findByCode(String code);

    boolean existsByCode(String code);
}
