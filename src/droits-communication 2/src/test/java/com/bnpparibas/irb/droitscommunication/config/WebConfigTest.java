package com.bnpparibas.irb.droitscommunication.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class WebConfigTest {

    private final WebConfig config = new WebConfig();

    @Test
    void objectMapper_devrait_serialiser_les_dates_en_ISO() throws Exception {
        ObjectMapper mapper = config.objectMapper();

        assertThat(mapper.isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)).isFalse();

        String json = mapper.writeValueAsString(LocalDate.of(2026, 6, 20));
        assertThat(json).contains("2026-06-20");
    }

    @Test
    void configureMessageConverters_devrait_ajouter_un_converter_json() {
        List<HttpMessageConverter<?>> converters = new ArrayList<>();

        config.configureMessageConverters(converters);

        assertThat(converters).hasSize(1);
        MappingJackson2HttpMessageConverter jsonConverter =
                (MappingJackson2HttpMessageConverter) converters.get(0);
        assertThat(jsonConverter.getSupportedMediaTypes())
                .contains(MediaType.APPLICATION_JSON, MediaType.APPLICATION_OCTET_STREAM);
    }

    @Test
    void addInterceptors_ne_devrait_pas_lever_d_exception() {
        // Implémentation vide (interceptor commenté) : on vérifie juste l'absence d'erreur.
        config.addInterceptors(new InterceptorRegistry());
    }

    @Test
    void customOpenAPI_devrait_definir_le_titre_et_le_schema_de_securite() {
        OpenAPI openAPI = config.customOpenAPI();

        assertThat(openAPI.getInfo().getTitle()).isEqualTo("Qlickflow API");
        assertThat(openAPI.getInfo().getVersion()).isEqualTo("v1");
        assertThat(openAPI.getComponents().getSecuritySchemes()).containsKey("oauth2Password");
        assertThat(openAPI.getSecurity()).isNotEmpty();
    }
}
