package com.bnpparibas.irb.qlickflow.wfdtrp.controller;
// ⚠️ TODO : ajuster le package selon l'emplacement réel des controllers.

import com.bnpparibas.irb.qlickflow.wfdtrp.dto.CommentaireDTO;
import com.bnpparibas.irb.qlickflow.wfdtrp.dto.CreateCommentaireDTO;
import com.bnpparibas.irb.qlickflow.wfdtrp.facade.CommentaireFacade;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentaireControllerTest {

    @Mock
    private CommentaireFacade commentaireFacade;

    @InjectMocks
    private CommentaireController controller;

    @Test
    void addCommentaire_returns201WithDto() {
        UUID requestId = UUID.randomUUID();
        CreateCommentaireDTO dto = CreateCommentaireDTO.builder().text("Mon commentaire").build();
        CommentaireDTO created = CommentaireDTO.builder()
                .id(UUID.randomUUID())
                .text("Mon commentaire")
                .author("First Last")
                .build();
        when(commentaireFacade.addCommentaire(dto, requestId)).thenReturn(created);

        var response = controller.addCommentaire(dto, requestId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(created);
    }

    @Test
    void getCommentaires_returns200WithList() {
        UUID requestId = UUID.randomUUID();
        List<CommentaireDTO> commentaires = List.of(
                CommentaireDTO.builder().id(UUID.randomUUID()).text("c1").build());
        when(commentaireFacade.getCommentaires(requestId)).thenReturn(commentaires);

        var response = controller.getCommentaires(requestId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(commentaires);
    }
}
