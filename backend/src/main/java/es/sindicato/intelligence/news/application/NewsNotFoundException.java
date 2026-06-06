package es.sindicato.intelligence.news.application;

public class NewsNotFoundException extends RuntimeException {

    public NewsNotFoundException(Long id) {
        super("news not found: " + id);
    }
}
