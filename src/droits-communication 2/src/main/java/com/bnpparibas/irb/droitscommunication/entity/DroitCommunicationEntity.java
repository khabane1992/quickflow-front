package com.bnpparibas.irb.droitscommunication.entity;

import com.bnpparibas.irb.droitscommunication.enums.CanalDemande;
import com.bnpparibas.irb.droitscommunication.enums.StatutDemande;
import com.bnpparibas.irb.droitscommunication.enums.TypeDemande;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Demande de droit de communication.
 * Correspond au formulaire "Nouvelle demande" : section informations + types + fichier.
 */
@Entity
@Table(name = "droits_communication")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class DroitCommunicationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- Section 1 : informations sur la demande ---

    /** Code de l'organisme (référentiel en base). */
    @Column(nullable = false)
    private String organisme;

    @Column(nullable = false)
    private String adressePostale;

    /** Code du complément d'adresse (référentiel en base), facultatif. */
    private String complementAdresse;

    @Column(nullable = false, length = 2000)
    private String objetDemande;

    /** Canal de réception (référentiel enum). */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private CanalDemande canalDemande;

    private String referenceDemande;

    @Column(nullable = false)
    private LocalDate dateReception;

    // --- Section "Type de demande" : multi-sélection ---

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "droit_communication_types",
            joinColumns = @JoinColumn(name = "droit_communication_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "type_demande", nullable = false, length = 50)
    @Builder.Default
    private Set<TypeDemande> typesDemande = new HashSet<>();

    // --- Section "Liste des droits de communication" : fichier joint ---

    /**
     * Référence du document gérée par le microservice d'upload (pas le binaire).
     * Nom et type conservés pour affichage/traçabilité.
     */
    private String documentReference;
    private String documentNom;
    private String documentContentType;

    // --- Suivi métier ---

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private StatutDemande statut = StatutDemande.SOUMISE;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime dateCreation;

    @LastModifiedDate
    private LocalDateTime dateModification;
}
