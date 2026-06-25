package com.bnpparibas.irb.droitscommunication.entity;

import com.bnpparibas.irb.droitscommunication.enums.Coherence;
import com.bnpparibas.irb.droitscommunication.enums.DecisionClient;
import com.bnpparibas.irb.droitscommunication.enums.PositionCompte;
import com.bnpparibas.irb.droitscommunication.enums.TypeIncoherence;
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

import java.math.BigDecimal;

/**
 * Client <b>consolidé</b> d'une demande : ligne du .xlsx enrichie par les résultats SAB,
 * produite par le batch (Phase 2). Porte la décision de l'initiateur (Phase 3).
 * Liée à la demande par {@code demandeId}.
 */
@Entity
@Table(name = "client_demande",
        indexes = @Index(name = "idx_client_demande", columnList = "demandeId"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientDemande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long demandeId;

    // --- Données fournies (colonnes du .xlsx) ---

    @Column(nullable = false)
    private String nomFourni;

    private String cin;
    private String numeroRc;

    // --- Enrichissement SAB ---

    private String idSab;
    private String nomSab;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Coherence coherence;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private TypeIncoherence typeIncoherence = TypeIncoherence.AUCUNE;

    private String numeroCompte;

    @Column(precision = 19, scale = 2)
    private BigDecimal solde;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private PositionCompte position;

    private String adresse;
    private String telephone;

    // --- Décision de l'initiateur (Phase 3) ---

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private DecisionClient decision;
}
