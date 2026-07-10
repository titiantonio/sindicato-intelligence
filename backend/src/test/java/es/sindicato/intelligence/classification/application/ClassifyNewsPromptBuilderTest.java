package es.sindicato.intelligence.classification.application;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ClassifyNewsPromptBuilderTest {

    @Test
    void buildsOfficialWf02PromptWithNewsUrlAndData() {
        ClassifyNewsPromptBuilder builder = new ClassifyNewsPromptBuilder();

        ClassifyNewsPrompt prompt = builder.build(
                "SIPRI publica adjudicaciones",
                "https://www.juntadeandalucia.es/educacion/sipri",
                "Resumen de la noticia",
                "Contenido de la noticia"
        );

        assertTrue(prompt.systemPrompt().contains("analista politico y laboral experto en educacion publica de Andalucia"));
        assertTrue(prompt.systemPrompt().contains("Responde exclusivamente con un objeto JSON valido"));
        assertTrue(prompt.systemPrompt().contains("revisa la URL y el contexto enriquecido"));
        assertTrue(prompt.systemPrompt().contains("subcategory FUERA_DE_AMBITO"));
        assertTrue(prompt.systemPrompt().contains("No generes keywords, entities ni summary"));
        assertTrue(prompt.userPrompt().contains("TITULO:"));
        assertTrue(prompt.userPrompt().contains("URL:"));
        assertTrue(prompt.userPrompt().contains("https://www.juntadeandalucia.es/educacion/sipri"));
        assertTrue(prompt.userPrompt().contains("SIPRI publica adjudicaciones"));
        assertTrue(prompt.userPrompt().contains("\"category\""));
        assertTrue(prompt.userPrompt().contains("Categorias permitidas para category"));
        assertTrue(prompt.userPrompt().contains("Criterios de relevance de 0 a 100"));
        assertTrue(prompt.userPrompt().contains("Reglas de descarte"));
        assertTrue(prompt.userPrompt().contains("usa la URL y el contexto enriquecido desde la URL"));
        assertTrue(prompt.userPrompt().contains("subcategory FUERA_DE_AMBITO"));
        assertTrue(prompt.userPrompt().contains("subcategory INFORMACION_INSUFICIENTE"));
        assertTrue(prompt.userPrompt().contains("No incluyas keywords, entities ni summary"));
        assertTrue(prompt.userPrompt().contains("Criterios de impact"));
        assertTrue(prompt.userPrompt().contains("Criterios de urgency"));
        assertTrue(prompt.userPrompt().contains("usa category OTROS y subcategory INFORMACION_INSUFICIENTE"));
    }

    @Test
    void handlesNullOptionalValues() {
        ClassifyNewsPromptBuilder builder = new ClassifyNewsPromptBuilder();

        ClassifyNewsPrompt prompt = builder.build("Titulo", "https://test.example/noticia", null, null);

        assertTrue(prompt.userPrompt().contains("Titulo"));
        assertTrue(prompt.userPrompt().contains("https://test.example/noticia"));
        assertTrue(prompt.userPrompt().contains("RESUMEN:"));
        assertTrue(prompt.userPrompt().contains("CONTENIDO:"));
    }
}
