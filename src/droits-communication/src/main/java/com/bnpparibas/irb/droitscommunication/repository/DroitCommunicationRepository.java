package com.bnpparibas.irb.droitscommunication.repository;

import com.bnpparibas.irb.droitscommunication.dto.StatutCount;
import com.bnpparibas.irb.droitscommunication.entity.DroitCommunicationEntity;
import com.bnpparibas.irb.droitscommunication.enums.StatutDemande;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DroitCommunicationRepository extends JpaRepository<DroitCommunicationEntity, Long> {

    /**
     * Listing paginé de "Mes demandes" avec filtres optionnels (statut, initiateur,
     * recherche plein-texte sur n° demande / référence / organisme).
     */
    @Query("""
            select d from DroitCommunicationEntity d
            where (:statut is null or d.statut = :statut)
              and (:initiateur is null or d.initiateur = :initiateur)
              and (
                    :q is null
                 or lower(d.numeroDemande)    like lower(concat('%', :q, '%'))
                 or lower(d.referenceDemande) like lower(concat('%', :q, '%'))
                 or lower(d.organisme)        like lower(concat('%', :q, '%'))
              )
            """)
    Page<DroitCommunicationEntity> search(@Param("statut") StatutDemande statut,
                                          @Param("initiateur") String initiateur,
                                          @Param("q") String q,
                                          Pageable pageable);

    /** Corbeille de validation : demandes en attente, filtrables par valideur affecté. */
    @Query("""
            select d from DroitCommunicationEntity d
            where d.statut = com.bnpparibas.irb.droitscommunication.enums.StatutDemande.EN_ATTENTE_VALIDATION
              and (:valideur is null or d.valideur = :valideur)
            """)
    Page<DroitCommunicationEntity> corbeilleValidation(@Param("valideur") String valideur, Pageable pageable);

    /** Compteurs par statut, pour les onglets de "Mes demandes". */
    @Query("""
            select new com.bnpparibas.irb.droitscommunication.dto.StatutCount(d.statut, count(d))
            from DroitCommunicationEntity d
            where (:initiateur is null or d.initiateur = :initiateur)
            group by d.statut
            """)
    List<StatutCount> compterParStatut(@Param("initiateur") String initiateur);
}
