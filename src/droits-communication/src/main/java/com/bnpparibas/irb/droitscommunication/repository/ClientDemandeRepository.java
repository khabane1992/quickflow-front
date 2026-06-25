package com.bnpparibas.irb.droitscommunication.repository;

import com.bnpparibas.irb.droitscommunication.entity.ClientDemande;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClientDemandeRepository extends JpaRepository<ClientDemande, Long> {

    List<ClientDemande> findByDemandeIdOrderById(Long demandeId);

    void deleteByDemandeId(Long demandeId);
}
