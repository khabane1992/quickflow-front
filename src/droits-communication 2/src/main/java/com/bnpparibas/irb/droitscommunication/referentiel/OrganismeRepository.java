package com.bnpparibas.irb.droitscommunication.referentiel;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrganismeRepository extends JpaRepository<Organisme, Long> {

    List<Organisme> findByActifTrueOrderByLibelleAsc();

    Optional<Organisme> findByCode(String code);

    boolean existsByCode(String code);
}
