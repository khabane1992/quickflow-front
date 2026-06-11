package com.bnpparibas.irb.qlickflow.wfdtrp.service;
// ⚠️ TODO : ajuster ce package selon l'emplacement réel de CommentaireServiceImpl.

import com.bnpparibas.irb.qlickflow.wfdtrp.dto.CreateCommentaireDTO;
import com.bnpparibas.irb.qlickflow.wfdtrp.entities.CommentaireEntity;
import com.bnpparibas.irb.qlickflow.wfdtrp.entities.DerogationRequest;
import com.bnpparibas.irb.qlickflow.wfdtrp.entities.user.ProfileEnum;
import com.bnpparibas.irb.qlickflow.wfdtrp.entities.user.UserEntity;
import com.bnpparibas.irb.qlickflow.wfdtrp.exceptions.ResourceNotFoundException;
import com.bnpparibas.irb.qlickflow.wfdtrp.repository.CommentaireRepository;
import com.bnpparibas.irb.qlickflow.wfdtrp.repository.DerogationRequestRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentaireServiceImplTest {

    @Mock
    private CommentaireRepository commentaireRepository;
    @Mock
    private DerogationRequestRepository derogationRequestRepository;
    @Mock
    private UserService userService;

    @InjectMocks
    private CommentaireServiceImpl commentaireService;

    @Test
    void addCommentaire_requestFound_buildsAndSavesCommentaire() {
        UUID requestId = UUID.randomUUID();
        DerogationRequest request = DerogationRequest.builder().businessKey("BK-001").build();
        when(derogationRequestRepository.findById(requestId)).thenReturn(Optional.of(request));
        UserEntity currentUser = UserEntity.builder()
                .uid("u1").email("u1@bnpparibas.com")
                .firstName("First").lastName("Last")
                .profile(ProfileEnum.CONSEILLER)
                .build();
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(commentaireRepository.save(any(CommentaireEntity.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        CreateCommentaireDTO dto = CreateCommentaireDTO.builder().text("Mon commentaire").build();

        CommentaireEntity saved = commentaireService.addCommentaire(dto, requestId);

        ArgumentCaptor<CommentaireEntity> captor = ArgumentCaptor.forClass(CommentaireEntity.class);
        verify(commentaireRepository).save(captor.capture());
        CommentaireEntity entity = captor.getValue();

        assertThat(entity.getText()).isEqualTo("Mon commentaire");
        assertThat(entity.getUser()).isEqualTo(currentUser);
        assertThat(entity.getDerogationRequest()).isEqualTo(request);
        assertThat(saved).isEqualTo(entity);
    }

    @Test
    void addCommentaire_requestNotFound_throwsResourceNotFound() {
        UUID requestId = UUID.randomUUID();
        when(derogationRequestRepository.findById(requestId)).thenReturn(Optional.empty());

        CreateCommentaireDTO dto = CreateCommentaireDTO.builder().text("x").build();

        assertThatThrownBy(() -> commentaireService.addCommentaire(dto, requestId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("non trouvée");
        verifyNoInteractions(commentaireRepository);
    }

    @Test
    void getCommentairesByRequestId_delegatesToRepository() {
        UUID requestId = UUID.randomUUID();
        List<CommentaireEntity> expected = List.of(
                CommentaireEntity.builder().id(UUID.randomUUID()).text("c1").build());
        when(commentaireRepository.findByDerogationRequestId(requestId)).thenReturn(expected);

        assertThat(commentaireService.getCommentairesByRequestId(requestId)).isEqualTo(expected);
    }
}
