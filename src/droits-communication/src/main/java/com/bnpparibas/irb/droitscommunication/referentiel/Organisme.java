package com.bnpparibas.irb.droitscommunication.referentiel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Référentiel évolutif "Organisme".
 * Stocké en base car la liste est alimentée/maintenue côté métier
 * et peut grandir sans redéploiement.
 * Alimente le drop-down "Organisme" du formulaire.
 */
@Entity
@Table(name = "ref_organisme")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Organisme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Code technique stable, utilisé comme valeur du drop-down. */
    @Column(nullable = false, unique = true, length = 50)
    private String code;

    /** Libellé affiché à l'utilisateur. */
    @Column(nullable = false, length = 255)
    private String libelle;

    /** Permet de masquer un organisme sans le supprimer. */
    @Column(nullable = false)
    private boolean actif = true;
}
