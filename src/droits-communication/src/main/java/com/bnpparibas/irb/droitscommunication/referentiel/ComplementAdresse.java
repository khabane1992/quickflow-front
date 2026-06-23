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
 * Référentiel évolutif "Complément d'adresse".
 * La maquette présente ce champ comme un drop-down -> on l'alimente depuis la base.
 */
@Entity
@Table(name = "ref_complement_adresse")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ComplementAdresse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(nullable = false, length = 255)
    private String libelle;

    @Column(nullable = false)
    private boolean actif = true;
}
