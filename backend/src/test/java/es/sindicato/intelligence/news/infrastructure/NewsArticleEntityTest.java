package es.sindicato.intelligence.news.infrastructure;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NewsArticleEntityTest {

    @Test
    void mapsToNewsArticlesTable() {
        Table table = NewsArticleEntity.class.getAnnotation(Table.class);

        assertNotNull(table);
        assertEquals("news_articles", table.name());
    }

    @Test
    void mapsIdWithIdentityStrategy() throws NoSuchFieldException {
        Field id = NewsArticleEntity.class.getDeclaredField("id");

        assertNotNull(id.getAnnotation(Id.class));
        assertEquals(GenerationType.IDENTITY, id.getAnnotation(GeneratedValue.class).strategy());
    }

    @Test
    void mapsColumnsToSchemaFields() throws NoSuchFieldException {
        assertColumn("sourceId", "source_id", false);
        assertColumn("title", "title", false);
        assertColumn("url", "url", false);
        assertColumn("summary", "summary", true);
        assertColumn("content", "content", true);
        assertColumn("hash", "hash", false);
        assertColumn("publishedAt", "published_at", true);
        assertColumn("capturedAt", "captured_at", false);
        assertColumn("processingStatus", "processing_status", false);
        assertColumn("createdAt", "created_at", false);
        assertColumn("updatedAt", "updated_at", false);
    }

    @Test
    void mapsStatusAsStringEnum() throws NoSuchFieldException {
        Field processingStatus = NewsArticleEntity.class.getDeclaredField("processingStatus");
        Enumerated enumerated = processingStatus.getAnnotation(Enumerated.class);

        assertNotNull(enumerated);
        assertEquals(EnumType.STRING, enumerated.value());
    }

    @Test
    void exposesProtectedNoArgsConstructorForJpa() throws NoSuchMethodException {
        Constructor<NewsArticleEntity> constructor = NewsArticleEntity.class.getDeclaredConstructor();

        assertTrue(Modifier.isProtected(constructor.getModifiers()));
    }

    private void assertColumn(String fieldName, String columnName, boolean nullable) throws NoSuchFieldException {
        Field field = NewsArticleEntity.class.getDeclaredField(fieldName);
        Column column = field.getAnnotation(Column.class);

        assertNotNull(column);
        assertEquals(columnName, column.name());
        assertEquals(nullable, column.nullable());
        assertFalse(Modifier.isFinal(field.getModifiers()));
    }
}
