package es.sindicato.intelligence.content.infrastructure;

import es.sindicato.intelligence.content.application.RelevantContentLink;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import java.net.URI;
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

    private HttpRelevantContentLinkExtractor extractor() {
        return new HttpRelevantContentLinkExtractor(
                RestClient.builder(),
                100,
                100,
                "ccoo.es,anpe.es,csif.es,ustea.es,ugt.es",
                "juntadeandalucia.es,gob.es,boe.es"
        );
    }
}
