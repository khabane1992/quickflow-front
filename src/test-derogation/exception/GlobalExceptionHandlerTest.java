package com.bnpparibas.irb.qlickflow.wfdtrp.controller.exception;
// Package confirmé par capture : controller.exception

import jakarta.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @Mock
    private HttpServletRequest request;

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleNotFoundException_returns404WithErrorMessage() {
        when(request.getRequestURI()).thenReturn("/api/v1/derog-tarif/derogation-requests/sabId");
        NotFoundException ex = new NotFoundException("Aucun client correspondant à l'ID SAB fourni");

        var response = handler.handleResourceNotFoundException(request, ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        // ErrorMessage contient le message d'origine et l'URI (structure exacte non capturée
        // → on vérifie via toString pour rester robuste ; affiner avec les getters réels si besoin)
        assertThat(response.getBody().toString()).contains("Aucun client correspondant");
    }

    @Test
    void handleDuplicateException_returns409WithErrorMessage() {
        when(request.getRequestURI()).thenReturn("/api/v1/derog-tarif/derogation-requests/create");
        DuplicateException ex = new DuplicateException("Doublon détecté");
        // TODO : si DuplicateException n'a pas de ctor (String), adapter l'instanciation

        var response = handler.handleDuplicateException(request, ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void notFoundException_keepsMessage() {
        NotFoundException ex = new NotFoundException("msg");
        assertThat(ex.getMessage()).isEqualTo("msg");
        assertThat(ex).isInstanceOf(RuntimeException.class);
    }
}
