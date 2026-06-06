package es.sindicato.intelligence.classification.application;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ClassifyNewsPromptBuilderTest {

    @Test
    void buildsOfficialWf02PromptWithNewsData() {
        ClassifyNewsPromptBuilder builder = new ClassifyNewsPromptBuilder();

        ClassifyNewsPrompt prompt = builder.build(
                "SIPRI publica adjudicaciones",
                "Resumen de la noticia",
                "Contenido de la noticia"
        );

        assertTrue(prompt.systemPrompt().contains("Eres un analista experto en educación pública de Andalucía."));
        assertTrue(prompt.systemPrompt().contains("Debes responder EXCLUSIVAMENTE en formato JSON válido."));
        assertTrue(prompt.userPrompt().contains("TÍTULO:"));
        assertTrue(prompt.userPrompt().contains("SIPRI publica adjudicaciones"));
        assertTrue(prompt.userPrompt().contains("\"category\""));
        assertTrue(prompt.userPrompt().contains("\"keywords\""));
    }

    @Test
    void handlesNullOptionalValues() {
        ClassifyNewsPromptBuilder builder = new ClassifyNewsPromptBuilder();

        ClassifyNewsPrompt prompt = builder.build("Titulo", null, null);

        assertTrue(prompt.userPrompt().contains("Titulo"));
        assertTrue(prompt.userPrompt().contains("RESUMEN:"));
        assertTrue(prompt.userPrompt().contains("CONTENIDO:"));
    }
}
