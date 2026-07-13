package es.sindicato.intelligence.classification.application;

import org.springframework.stereotype.Component;

@Component
public class ClassifyNewsPromptBuilder {

    private static final String SYSTEM_PROMPT = """
            Actuas como analista politico y laboral experto en educacion publica de Andalucia para un sindicato de docentes.

            Tu tarea es evaluar noticias de prensa, comunicados y boletines oficiales, clasificando solo con la informacion proporcionada y con la URL indicada cuando se haya podido aportar contexto complementario desde ella.

            Prioriza el impacto directo sobre profesorado andaluz, bolsas de trabajo, SIPRI, oposiciones, plantillas, ratios, retribuciones, horarios, normativa de la Junta de Andalucia, mesas sectoriales, conflictos laborales y actividad sindical docente.

            Reglas estrictas de formato:
            1. Responde exclusivamente con un objeto JSON valido.
            2. No incluyas introducciones, explicaciones externas ni conclusiones fuera del JSON.
            3. No uses markdown ni bloques de codigo.
            4. Usa exactamente las claves solicitadas y valores compatibles con el contrato.
            5. Si hay comillas internas en textos, deben quedar correctamente escapadas.

            Si la noticia no contiene informacion suficiente para clasificarla, revisa la URL y el contexto enriquecido aportado desde esa URL si existe. Si aun asi no hay datos suficientes, devuelve solo JSON minimo valido con category OTROS, subcategory INFORMACION_INSUFICIENTE, relevance 0, impact LOW y urgency LOW. No generes keywords, entities ni summary.

            Si la noticia esta fuera del ambito del sistema, devuelve solo JSON minimo valido con category OTROS, subcategory FUERA_DE_AMBITO, relevance 0, impact LOW y urgency LOW. No generes keywords, entities ni summary.
            """;

    public ClassifyNewsPrompt build(String title, String url, String summary, String content) {
        String userPrompt = """
                Analiza la siguiente noticia:

                TITULO:
                %s

                URL:
                %s

                RESUMEN:
                %s

                CONTENIDO:
                %s

                Devuelve:

                {
                  "category": "",
                  "subcategory": "",
                  "relevance": 0,
                  "impact": "",
                  "urgency": "",
                  "classificationReason": ""
                }

                Categorias permitidas para category:
                OPOSICIONES, INTERINOS, SIPRI, PLANTILLAS, RETRIBUCIONES, FORMACION, INSPECCION, LEGISLACION, CURRICULO, UNIVERSIDAD, FP, DIGITALIZACION, INCLUSION, INFRAESTRUCTURAS, CONFLICTO_LABORAL, SINDICAL, OTROS.

                Criterios de relevance de 0 a 100:
                - 90-100: impacto critico y directo sobre empleo, estabilidad, retribuciones, horarios, oposiciones, bolsas SIPRI, BOJA laboral o huelgas educativas en Andalucia.
                - 70-89: impacto alto sobre docentes andaluces, mesas sectoriales, ratios, plantillas, adjudicaciones, normativa educativa o decisiones de la Consejeria.
                - 40-69: impacto moderado por planes educativos, curriculo, FP, inclusion, digitalizacion, infraestructuras o medidas con efecto indirecto en centros andaluces.
                - 10-39: impacto bajo, opinion, informacion generica, universidad o educacion fuera de Andalucia sin efecto laboral docente claro.
                - 0: noticia fuera de ambito, informacion insuficiente o noticia no clasificable con los datos recibidos.

                Reglas de descarte:
                - Si la noticia no trata sobre educacion, profesorado, sindicatos docentes, normativa educativa, empleo docente, centros educativos, Junta de Andalucia, universidad, FP o condiciones laborales docentes, clasificala como category OTROS, subcategory FUERA_DE_AMBITO, relevance 0, impact LOW, urgency LOW.
                - Si la noticia podria estar relacionada pero el titulo, resumen y contenido no aportan datos suficientes para decidirlo, usa la URL y el contexto enriquecido desde la URL si se ha incluido en CONTENIDO. Si tampoco aporta datos verificables, clasificala como category OTROS, subcategory INFORMACION_INSUFICIENTE, relevance 0, impact LOW, urgency LOW.
                - Para FUERA_DE_AMBITO o INFORMACION_INSUFICIENTE devuelve solo category, subcategory, relevance, impact y urgency. No incluyas keywords, entities ni summary.
                - No uses FUERA_DE_AMBITO para noticias educativas de baja relevancia; en ese caso usa la categoria mas cercana, relevance 10-39, impact LOW y urgency LOW.

                Criterios de impact:
                - CRITICAL: oposiciones docentes andaluzas, bolsas extraordinarias, SIPRI, cambios BOJA sobre retribuciones/horarios/estabilidad o huelgas generales educativas.
                - HIGH: mesas sectoriales, ratios, plantillas, adjudicaciones de destinos, conflictos laborales relevantes o normativa con impacto operativo claro.
                - MEDIUM: cambios educativos generales, curriculo, FP, inclusion, digitalizacion, inspeccion, infraestructuras o medidas con impacto indirecto.
                - LOW: opinion, informacion generica, universidad, noticias fuera de Andalucia o informacion insuficiente.

                Criterios de urgency:
                - HIGH: plazos abiertos, convocatorias, adjudicaciones, huelgas, BOJA reciente o decisiones que exigen accion inmediata.
                - MEDIUM: seguimiento necesario a corto plazo aunque no haya accion inmediata.
                - LOW: informacion de contexto, baja prioridad o informacion insuficiente.

                Para noticias clasificables, rellena subcategory con una etiqueta corta y concreta. Solo en noticias clasificables puedes anadir summary con maximo dos frases, keywords, entities y classificationReason con una frase breve que justifique la categoria, relevancia, impacto y urgencia.

                Si el titulo, resumen, contenido y contexto enriquecido desde la URL no permiten inferir una tematica educativa concreta, no rechaces la tarea y no expliques fuera del JSON: usa category OTROS y subcategory INFORMACION_INSUFICIENTE.
                """.formatted(safe(title), safe(url), safe(summary), safe(content));

        return new ClassifyNewsPrompt(SYSTEM_PROMPT, userPrompt);
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}
