package com.bnpparibas.irb.droitscommunication.mapper;

import com.bnpparibas.irb.droitscommunication.dto.ClientDemandeResponse;
import com.bnpparibas.irb.droitscommunication.dto.DroitCommunicationRequest;
import com.bnpparibas.irb.droitscommunication.dto.DroitCommunicationResponse;
import com.bnpparibas.irb.droitscommunication.dto.DroitCommunicationSummary;
import com.bnpparibas.irb.droitscommunication.entity.ClientDemande;
import com.bnpparibas.irb.droitscommunication.entity.DroitCommunicationEntity;
import com.bnpparibas.irb.droitscommunication.enums.CanalDemande;
import com.bnpparibas.irb.droitscommunication.enums.Coherence;
import com.bnpparibas.irb.droitscommunication.enums.DecisionClient;
import com.bnpparibas.irb.droitscommunication.enums.PositionCompte;
import com.bnpparibas.irb.droitscommunication.enums.StatutDemande;
import com.bnpparibas.irb.droitscommunication.enums.TypeDemande;
import com.bnpparibas.irb.droitscommunication.enums.TypeIncoherence;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-25T14:42:49+0100",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.15 (Amazon.com Inc.)"
)
@Component
public class DroitCommunicationMapperImpl implements DroitCommunicationMapper {

    @Override
    public DroitCommunicationEntity toEntity(DroitCommunicationRequest request) {
        if ( request == null ) {
            return null;
        }

        DroitCommunicationEntity.DroitCommunicationEntityBuilder droitCommunicationEntity = DroitCommunicationEntity.builder();

        droitCommunicationEntity.organisme( request.organisme() );
        droitCommunicationEntity.adressePostale( request.adressePostale() );
        droitCommunicationEntity.complementAdresse( request.complementAdresse() );
        droitCommunicationEntity.objetDemande( request.objetDemande() );
        droitCommunicationEntity.canalDemande( request.canalDemande() );
        droitCommunicationEntity.referenceDemande( request.referenceDemande() );
        droitCommunicationEntity.dateReception( request.dateReception() );
        Set<TypeDemande> set = request.typesDemande();
        if ( set != null ) {
            droitCommunicationEntity.typesDemande( new LinkedHashSet<TypeDemande>( set ) );
        }
        droitCommunicationEntity.documentId( request.documentId() );
        droitCommunicationEntity.initiateur( request.initiateur() );

        return droitCommunicationEntity.build();
    }

    @Override
    public DroitCommunicationResponse toResponse(DroitCommunicationEntity entity, List<ClientDemandeResponse> clients) {
        if ( entity == null && clients == null ) {
            return null;
        }

        Long id = null;
        String numeroDemande = null;
        UUID businessKey = null;
        String organisme = null;
        String adressePostale = null;
        String complementAdresse = null;
        String objetDemande = null;
        CanalDemande canalDemande = null;
        String referenceDemande = null;
        LocalDate dateReception = null;
        Set<TypeDemande> typesDemande = null;
        UUID documentId = null;
        int nombreClients = 0;
        StatutDemande statut = null;
        String initiateur = null;
        String valideur = null;
        String motifRejet = null;
        LocalDateTime dateCreation = null;
        LocalDateTime dateModification = null;
        if ( entity != null ) {
            id = entity.getId();
            numeroDemande = entity.getNumeroDemande();
            businessKey = entity.getBusinessKey();
            organisme = entity.getOrganisme();
            adressePostale = entity.getAdressePostale();
            complementAdresse = entity.getComplementAdresse();
            objetDemande = entity.getObjetDemande();
            canalDemande = entity.getCanalDemande();
            referenceDemande = entity.getReferenceDemande();
            dateReception = entity.getDateReception();
            Set<TypeDemande> set = entity.getTypesDemande();
            if ( set != null ) {
                typesDemande = new LinkedHashSet<TypeDemande>( set );
            }
            documentId = entity.getDocumentId();
            nombreClients = entity.getNombreClients();
            statut = entity.getStatut();
            initiateur = entity.getInitiateur();
            valideur = entity.getValideur();
            motifRejet = entity.getMotifRejet();
            dateCreation = entity.getDateCreation();
            dateModification = entity.getDateModification();
        }
        List<ClientDemandeResponse> clients1 = null;
        List<ClientDemandeResponse> list = clients;
        if ( list != null ) {
            clients1 = new ArrayList<ClientDemandeResponse>( list );
        }

        DroitCommunicationResponse droitCommunicationResponse = new DroitCommunicationResponse( id, numeroDemande, businessKey, organisme, adressePostale, complementAdresse, objetDemande, canalDemande, referenceDemande, dateReception, typesDemande, documentId, nombreClients, statut, initiateur, valideur, motifRejet, clients1, dateCreation, dateModification );

        return droitCommunicationResponse;
    }

    @Override
    public DroitCommunicationSummary toSummary(DroitCommunicationEntity entity) {
        if ( entity == null ) {
            return null;
        }

        Long id = null;
        String numeroDemande = null;
        LocalDate dateReception = null;
        String referenceDemande = null;
        String organisme = null;
        CanalDemande canalDemande = null;
        int nombreClients = 0;
        StatutDemande statut = null;
        String initiateur = null;
        String valideur = null;

        id = entity.getId();
        numeroDemande = entity.getNumeroDemande();
        dateReception = entity.getDateReception();
        referenceDemande = entity.getReferenceDemande();
        organisme = entity.getOrganisme();
        canalDemande = entity.getCanalDemande();
        nombreClients = entity.getNombreClients();
        statut = entity.getStatut();
        initiateur = entity.getInitiateur();
        valideur = entity.getValideur();

        DroitCommunicationSummary droitCommunicationSummary = new DroitCommunicationSummary( id, numeroDemande, dateReception, referenceDemande, organisme, canalDemande, nombreClients, statut, initiateur, valideur );

        return droitCommunicationSummary;
    }

    @Override
    public ClientDemandeResponse toClientResponse(ClientDemande client) {
        if ( client == null ) {
            return null;
        }

        Long id = null;
        String nomFourni = null;
        String cin = null;
        String numeroRc = null;
        String idSab = null;
        String nomSab = null;
        Coherence coherence = null;
        TypeIncoherence typeIncoherence = null;
        String numeroCompte = null;
        BigDecimal solde = null;
        PositionCompte position = null;
        String adresse = null;
        String telephone = null;
        DecisionClient decision = null;

        id = client.getId();
        nomFourni = client.getNomFourni();
        cin = client.getCin();
        numeroRc = client.getNumeroRc();
        idSab = client.getIdSab();
        nomSab = client.getNomSab();
        coherence = client.getCoherence();
        typeIncoherence = client.getTypeIncoherence();
        numeroCompte = client.getNumeroCompte();
        solde = client.getSolde();
        position = client.getPosition();
        adresse = client.getAdresse();
        telephone = client.getTelephone();
        decision = client.getDecision();

        ClientDemandeResponse clientDemandeResponse = new ClientDemandeResponse( id, nomFourni, cin, numeroRc, idSab, nomSab, coherence, typeIncoherence, numeroCompte, solde, position, adresse, telephone, decision );

        return clientDemandeResponse;
    }
}
