package com.bnpparibas.irb.qlickflow.wfdtrp.facade.impl;
// ⚠️ TODO : ajuster le package selon l'emplacement réel de CommentaireFacadeImpl.

import com.bnpparibas.irb.qlickflow.wfdtrp.dto.CommentaireDTO;
import com.bnpparibas.irb.qlickflow.wfdtrp.dto.CreateCommentaireDTO;
import com.bnpparibas.irb.qlickflow.wfdtrp.entities.CommentaireEntity;
import com.bnpparibas.irb.qlickflow.wfdtrp.mapper.CommentaireMapper;
import com.bnpparibas.irb.qlickflow.wfdtrp.service.CommentaireService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentaireFacadeImplTest {

    @Mock
    private CommentaireService commentaireService;
    @Mock
    private CommentaireMapper commentaireMapper;

    @InjectMocks
    private CommentaireFacadeImpl facade;

    @Test
    void addCommentaire_mapsSavedEntity() {
        UUID requestId = UUID.randomUUID();
        CreateCommentaireDTO dto = CreateCommentaireDTO.builder().text("c").build();
        CommentaireEntity saved = CommentaireEntity.builder().id(UUID.randomUUID()).text("c").build();
        CommentaireDTO mapped = CommentaireDTO.builder().id(saved.getId()).text("c").build();
        when(commentaireService.addCommentaire(dto, requestId)).thenReturn(saved);
        when(commentaireMapper.toDTO(saved)).thenReturn(mapped);

        assertThat(facade.addCommentaire(dto, requestId)).isEqualTo(mapped);
    }

    @Test
    void getCommentaires_mapsEachEntity() {
        UUID requestId = UUID.randomUUID();
        CommentaireEntity e1 = CommentaireEntity.builder().id(UUID.randomUUID()).text("c1").build();
        CommentaireEntity e2 = CommentaireEntity.builder().id(UUID.randomUUID()).text("c2").build();
        when(commentaireService.getCommentairesByRequestId(requestId)).thenReturn(List.of(e1, e2));
        when(commentaireMapper.toDTO(e1)).thenReturn(CommentaireDTO.builder().text("c1").build());
        when(commentaireMapper.toDTO(e2)).thenReturn(CommentaireDTO.builder().text("c2").build());

        List<CommentaireDTO> result = facade.getCommentaires(requestId);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(CommentaireDTO::getText).containsExactly("c1", "c2");
    }

    @Test
    void getCommentaires_empty_returnsEmptyList() {
        UUID requestId = UUID.randomUUID();
        when(commentaireService.getCommentairesByRequestId(requestId)).thenReturn(List.of());

        assertThat(facade.getCommentaires(requestId)).isEmpty();
    }
}
