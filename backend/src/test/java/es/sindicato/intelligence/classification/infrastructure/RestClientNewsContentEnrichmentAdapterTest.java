package es.sindicato.intelligence.classification.infrastructure;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RestClientNewsContentEnrichmentAdapterTest {

    @Test
    void rejectsUnsafeLocalUrlsWithoutRequestingThem() {
        RestClientNewsContentEnrichmentAdapter adapter = new RestClientNewsContentEnrichmentAdapter(
                RestClient.builder(),
                100,
                100,
                1000
        );

        assertEquals(java.util.Optional.empty(), adapter.enrich("http://localhost/admin"));
        assertEquals(java.util.Optional.empty(), adapter.enrich("file:///etc/passwd"));
    }
}
