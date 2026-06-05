package es.sindicato.intelligence.source.application;

public class SourceNotFoundException extends RuntimeException {

    public SourceNotFoundException(Long id) {
        super("source not found: " + id);
    }
}
