package com.bnpparibas.irb.droitscommunication.document;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.UUID;

/**
 * Implémentation de simulation du {@link DocumentContentPort} : génère un .xlsx de test
 * (en-tête + ~250 lignes) avec les colonnes attendues, pour faire tourner le batch de
 * bout en bout en local tant que le vrai MS Document n'est pas branché.
 */
@Component
@Slf4j
public class DocumentContentStub implements DocumentContentPort {

    /** En-tête attendu (à confirmer avec le métier). */
    static final String[] ENTETE = {"Nom & Prénom / Raison Sociale", "CIN", "Numéro RC"};

    private static final int NB_LIGNES = 250;

    @Override
    public byte[] telecharger(UUID documentId) {
        log.info("[STUB MS Document] download documentId={} -> .xlsx simulé ({} lignes)", documentId, NB_LIGNES);
        try (XSSFWorkbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = wb.createSheet("Clients");

            Row header = sheet.createRow(0);
            for (int c = 0; c < ENTETE.length; c++) {
                header.createCell(c).setCellValue(ENTETE[c]);
            }

            for (int i = 1; i <= NB_LIGNES; i++) {
                Row row = sheet.createRow(i);
                boolean morale = i % 5 == 0;
                if (morale) {
                    row.createCell(0).setCellValue("STE CLIENT " + i + " SARL");
                    row.createCell(1).setCellValue("");
                    row.createCell(2).setCellValue("RC" + String.format("%05d", i));
                } else {
                    row.createCell(0).setCellValue("Client Prénom " + i);
                    row.createCell(1).setCellValue("CIN" + String.format("%06d", i));
                    row.createCell(2).setCellValue("");
                }
            }

            wb.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException("Échec de génération du .xlsx simulé", e);
        }
    }
}
