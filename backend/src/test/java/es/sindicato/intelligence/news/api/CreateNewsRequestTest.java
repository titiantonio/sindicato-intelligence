package es.sindicato.intelligence.news.api;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CreateNewsRequestTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void acceptsValidRequest() {
        CreateNewsRequest request = new CreateNewsRequest(
                1L,
                "Convocatoria docente",
                "https://test.example/news/1",
                "Resumen",
                "Contenido",
                OffsetDateTime.parse("2026-06-06T09:00:00Z")
        );

        assertTrue(validator.validate(request).isEmpty());
    }

    @Test
    void rejectsInvalidRequest() {
        CreateNewsRequest request = new CreateNewsRequest(
                0L,
                "",
                "not-a-url",
                "Resumen",
                "Contenido",
                null
        );

        assertFalse(validator.validate(request).isEmpty());
    }
}
