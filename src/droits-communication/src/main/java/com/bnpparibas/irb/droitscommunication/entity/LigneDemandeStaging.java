package com.bnpparibas.irb.droitscommunication.entity;

import com.bnpparibas.irb.droitscommunication.enums.StatutLigne;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Table <b>tampon</b> (staging) : une ligne brute du fichier .xlsx, telle que parsée.
 * Le batch lit ces lignes (statut EN_ATTENTE), les envoie à SAB par lots, puis les
 * marque TRAITE/ERREUR. Découplée de la demande par un simple {@code demandeId} pour
 * un reader batch léger.
 */
@Entity
@Table(name = "ligne_demande_staging",
        indexes = @Index(name = "idx_staging_demande_statut", columnList = "demandeId,statutLigne"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LigneDemandeStaging {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long demandeId;

    /** Numéro de ligne dans le fichier (1-based, hors en-tête). */
    private int numeroLigne;

    // --- Colonnes du .xlsx ---

    @Column(nullable = false)
    private String nomRaisonSociale;

    private String cin;
    private String numeroRc;

    // --- Suivi du traitement ---

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private StatutLigne statutLigne = StatutLigne.EN_ATTENTE;

    @Column(length = 500)
    private String messageErreur;
}
