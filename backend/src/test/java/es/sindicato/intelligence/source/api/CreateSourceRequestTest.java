package es.sindicato.intelligence.source.api;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CreateSourceRequestTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void acceptsValidRequest() {
        CreateSourceRequest request = new CreateSourceRequest(
                "BOJA",
                "https://www.juntadeandalucia.es/boja",
                "RSS",
                10,
                true
        );

        assertTrue(validator.validate(request).isEmpty());
    }

    @Test
    void rejectsInvalidRequest() {
        CreateSourceRequest request = new CreateSourceRequest(
                "",
                "not-a-url",
                "",
                -1,
                null
        );

        assertFalse(validator.validate(request).isEmpty());
    }
}
