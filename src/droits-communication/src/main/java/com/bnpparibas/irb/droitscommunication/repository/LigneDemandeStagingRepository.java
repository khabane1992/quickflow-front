package com.bnpparibas.irb.droitscommunication.repository;

import com.bnpparibas.irb.droitscommunication.entity.LigneDemandeStaging;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LigneDemandeStagingRepository extends JpaRepository<LigneDemandeStaging, Long> {

    long countByDemandeId(Long demandeId);

    void deleteByDemandeId(Long demandeId);
}
