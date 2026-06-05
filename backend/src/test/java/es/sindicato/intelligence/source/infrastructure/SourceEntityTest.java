package es.sindicato.intelligence.source.infrastructure;

import jakarta.persistence.Column;
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

class SourceEntityTest {

    @Test
    void mapsToSourcesTable() {
        Table table = SourceEntity.class.getAnnotation(Table.class);

        assertNotNull(table);
        assertEquals("sources", table.name());
    }

    @Test
    void mapsIdWithIdentityStrategy() throws NoSuchFieldException {
        Field id = SourceEntity.class.getDeclaredField("id");

        assertNotNull(id.getAnnotation(Id.class));
        assertEquals(GenerationType.IDENTITY, id.getAnnotation(GeneratedValue.class).strategy());
    }

    @Test
    void mapsColumnsToSchemaFields() throws NoSuchFieldException {
        assertColumn("name", "name", false);
        assertColumn("url", "url", false);
        assertColumn("type", "type", false);
        assertColumn("priority", "priority", false);
        assertColumn("active", "active", false);
        assertColumn("createdAt", "created_at", false);
        assertColumn("updatedAt", "updated_at", false);
    }

    @Test
    void exposesProtectedNoArgsConstructorForJpa() throws NoSuchMethodException {
        Constructor<SourceEntity> constructor = SourceEntity.class.getDeclaredConstructor();

        assertTrue(Modifier.isProtected(constructor.getModifiers()));
    }

    private void assertColumn(String fieldName, String columnName, boolean nullable) throws NoSuchFieldException {
        Field field = SourceEntity.class.getDeclaredField(fieldName);
        Column column = field.getAnnotation(Column.class);

        assertNotNull(column);
        assertEquals(columnName, column.name());
        assertEquals(nullable, column.nullable());
        assertFalse(Modifier.isFinal(field.getModifiers()));
    }
}
