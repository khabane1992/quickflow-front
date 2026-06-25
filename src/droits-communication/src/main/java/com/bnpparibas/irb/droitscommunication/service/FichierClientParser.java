package com.bnpparibas.irb.droitscommunication.service;

import com.bnpparibas.irb.droitscommunication.dto.LigneFichierClient;
import com.bnpparibas.irb.droitscommunication.exception.FichierInvalideException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Parse le fichier .xlsx de la liste des droits (colonnes attendues :
 * Nom & Prénom / Raison Sociale, CIN, Numéro RC). En-tête sur la 1re ligne,
 * lecture par index de colonne, lignes vides ignorées.
 */
@Service
public class FichierClientParser {

    private static final DataFormatter FORMATTER = new DataFormatter();
    private static final int COL_NOM = 0;
    private static final int COL_CIN = 1;
    private static final int COL_RC = 2;

    public List<LigneFichierClient> parser(byte[] contenu) {
        if (contenu == null || contenu.length == 0) {
            throw new FichierInvalideException("Fichier .xlsx vide");
        }
        List<LigneFichierClient> lignes = new ArrayList<>();
        try (Workbook wb = new XSSFWorkbook(new ByteArrayInputStream(contenu))) {
            Sheet sheet = wb.getSheetAt(0);
            int numero = 0;
            // On saute la ligne d'en-tête (index 0).
            for (int r = 1; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);
                if (row == null) {
                    continue;
                }
                String nom = lire(row, COL_NOM);
                String cin = lire(row, COL_CIN);
                String rc = lire(row, COL_RC);
                if (!StringUtils.hasText(nom) && !StringUtils.hasText(cin) && !StringUtils.hasText(rc)) {
                    continue; // ligne vide
                }
                if (!StringUtils.hasText(nom)) {
                    throw new FichierInvalideException(
                            "Ligne " + (r + 1) + " : nom / raison sociale manquant");
                }
                numero++;
                lignes.add(new LigneFichierClient(numero, nom,
                        StringUtils.hasText(cin) ? cin : null,
                        StringUtils.hasText(rc) ? rc : null));
            }
        } catch (IOException e) {
            throw new FichierInvalideException("Fichier .xlsx illisible", e);
        }
        if (lignes.isEmpty()) {
            throw new FichierInvalideException("Aucune ligne client dans le fichier .xlsx");
        }
        return lignes;
    }

    private static String lire(Row row, int col) {
        Cell cell = row.getCell(col);
        return cell == null ? "" : FORMATTER.formatCellValue(cell).trim();
    }
}
