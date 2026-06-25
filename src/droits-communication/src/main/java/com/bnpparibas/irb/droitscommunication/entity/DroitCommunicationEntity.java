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
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Demande de droit de communication.
 * Correspond au formulaire "Nouvelle demande" (informations + types + fichier .xlsx joint)
 * et porte le statut du cycle de vie de la maquette "Mes demandes".
 *
 * <p>Phase 1 : à la création on stocke uniquement les métadonnées + le <b>chemin</b> du
 * fichier .xlsx (pas de parsing). Le parsing de la liste clients et l'enrichissement SAB
 * sont gérés par le batch en Phase 2.
 */
@Entity
@Table(name = "droits_communication")
@Audited
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

    /** Identifiant métier affiché (ex. "DC-2026-0042"), généré à la création. */
    @Column(unique = true, length = 30)
    private String numeroDemande;

    /**
     * Clé métier technique (UUID) générée à la création.
     * Sert d'{@code ownerRef} auprès du microservice Document et de {@code businessKey}
     * pour l'instance de process Camunda (Phase 3). Stable et découplée du numéro affiché.
     */
    @Column(nullable = false, unique = true, updatable = false)
    private UUID businessKey;

    // --- Section 1 : informations sur la demande ---

    /** Code de l'organisme (référentiel en base). */
    @Column(nullable = false)
    private String organisme;

    /** Adresse postale de l'organisme — facultative (maquette : pas d'astérisque). */
    private String adressePostale;

    /** Complément d'adresse en texte libre (maquette : "Bâtiment, étage, etc."). */
    private String complementAdresse;

    /** Objet de la demande — facultatif, 255 caractères max (maquette). */
    @Column(length = 255)
    private String objetDemande;

    /** Canal de réception (référentiel enum : Mail / Courrier). */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CanalDemande canalDemande;

    /** Référence de la demande émise par l'organisme — obligatoire (maquette). */
    @Column(nullable = false)
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

    // --- Section "Liste des droits de communication" : fichier .xlsx joint ---

    /**
     * Identifiant (UUID) du fichier .xlsx dans le microservice Document (source de vérité).
     * Le front a uploadé le fichier au MS Document (draft), qui a renvoyé cet UUID ; à la
     * création on le <b>commit</b> (ownerRef = businessKey, module = DC). Le binaire et ses
     * métadonnées (nom, type, taille) restent côté MS Document ; le batch le récupérera via
     * cet id en Phase 2.
     */
    @Column(nullable = false)
    private UUID documentId;

    /**
     * Nombre de clients de la liste (colonne "Clts" de la maquette).
     * Non renseigné en Phase 1 : alimenté par le batch après parsing du .xlsx (Phase 2).
     */
    @Column(nullable = false)
    @Builder.Default
    private int nombreClients = 0;

    // --- Suivi métier / workflow ---

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private StatutDemande statut = StatutDemande.TRAITEMENT_SAB;

    /** Initiateur de la demande (owner). Sans auth : fourni à la création. */
    private String initiateur;

    /** Valideur affecté quand la demande est EN_ATTENTE_VALIDATION (Phase 3). */
    private String valideur;

    /** Motif saisi par le valideur en cas de rejet (Phase 3). */
    @Column(length = 500)
    private String motifRejet;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime dateCreation;

    @LastModifiedDate
    private LocalDateTime dateModification;
}
