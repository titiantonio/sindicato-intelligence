package es.sindicato.intelligence.content.infrastructure;

import es.sindicato.intelligence.content.application.RelevantContentLink;
import es.sindicato.intelligence.news.domain.NewsArticle;
import es.sindicato.intelligence.news.domain.NewsStatus;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class HttpRelevantContentLinkExtractorTest {

    @Test
    void extractsOfficialDocumentLinks() {
        HttpRelevantContentLinkExtractor extractor = extractor();
        String html = """
                <html><body>
                  <a href="/educacion/documentos/resolucion.pdf">Resolucion oficial</a>
                  <a href="https://www.juntadeandalucia.es/educacion/consulta/listado">Consulta de listados</a>
                </body></html>
                """;

        List<RelevantContentLink> links = extractor.extractFromHtml(127L, URI.create("https://www.juntadeandalucia.es/educacion/noticia"), html);

        assertEquals(2, links.size());
        assertEquals("Resolucion oficial", links.get(0).label());
        assertEquals("https://www.juntadeandalucia.es/educacion/documentos/resolucion.pdf", links.get(0).url());
        assertEquals("https://www.juntadeandalucia.es/educacion/consulta/listado", links.get(1).url());
    }

    @Test
    void includesOfficialDocumentWhenNewsUrlIsDirectPdf() {
        HttpRelevantContentLinkExtractor extractor = extractor();
        NewsArticle newsArticle = newsArticle(
                "Resolución de 22 de julio de 2026 de la Junta de Andalucía",
                "http://www.juntadeandalucia.es/boja/2026/214001/BOJA26-214001-00002-9998-01_00341229.pdf"
        );

        List<RelevantContentLink> links = extractor.extract(List.of(newsArticle));

        assertEquals(1, links.size());
        assertEquals(44L, links.getFirst().newsId());
        assertEquals("Resolución de 22 de julio de 2026 de la Junta de Andalucía", links.getFirst().label());
        assertEquals("http://www.juntadeandalucia.es/boja/2026/214001/BOJA26-214001-00002-9998-01_00341229.pdf", links.getFirst().url());
    }

    @Test
    void excludesLinksToOtherUnions() {
        HttpRelevantContentLinkExtractor extractor = extractor();
        String html = """
                <a href="https://www.ccoo.es/documentos/resolucion.pdf">Documento sindicato</a>
                <a href="https://www.juntadeandalucia.es/educacion/anexos/anexo.pdf">Anexo oficial</a>
                """;

        List<RelevantContentLink> links = extractor.extractFromHtml(127L, URI.create("https://www.juntadeandalucia.es/educacion/noticia"), html);

        assertEquals(1, links.size());
        assertEquals("https://www.juntadeandalucia.es/educacion/anexos/anexo.pdf", links.getFirst().url());
    }

    @Test
    void rejectsUnsafeLocalLinks() {
        HttpRelevantContentLinkExtractor extractor = extractor();
        String html = """
                <a href="http://localhost/admin/resolucion.pdf">Localhost</a>
                <a href="http://127.0.0.1/admin/resolucion.pdf">Loopback</a>
                """;

        List<RelevantContentLink> links = extractor.extractFromHtml(127L, URI.create("https://www.juntadeandalucia.es/educacion/noticia"), html);

        assertFalse(extractor.isSafePublicHttpUri("http://localhost/admin"));
        assertEquals(List.of(), links);
    }

    @Test
    void skipsMalformedSharingLinksAndContinuesExtractingOfficialLinks() {
        HttpRelevantContentLinkExtractor extractor = extractor();
        String html = """
                <a href="https://twitter.com/share?text=Oposiciones docentes 2026 de acceso al subgrupo A1 en Andalucia baremo provisional de la fase de concurso">Compartir</a>
                <a href="https://www.juntadeandalucia.es/educacion/baremo-provisional.pdf">Baremo provisional</a>
                """;

        List<RelevantContentLink> links = extractor.extractFromHtml(127L, URI.create("https://www.juntadeandalucia.es/educacion/noticia"), html);

        assertEquals(1, links.size());
        assertEquals("Baremo provisional", links.getFirst().label());
        assertEquals("https://www.juntadeandalucia.es/educacion/baremo-provisional.pdf", links.getFirst().url());
    }

    private HttpRelevantContentLinkExtractor extractor() {
        return new HttpRelevantContentLinkExtractor(
                RestClient.builder(),
                100,
                100,
                "ccoo.es,anpe.es,csif.es,ustea.es,ugt.es",
                "juntadeandalucia.es,gob.es,boe.es"
        );
    }

    private NewsArticle newsArticle(String title, String url) {
        OffsetDateTime now = OffsetDateTime.parse("2026-07-24T10:00:00Z");
        return new NewsArticle(
                44L,
                4L,
                title,
                url,
                "Resumen",
                "",
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                now,
                now,
                NewsStatus.EVENT_MATCHED,
                now,
                now
        );
    }
}
