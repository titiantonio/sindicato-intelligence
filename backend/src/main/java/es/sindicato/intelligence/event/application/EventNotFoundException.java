package es.sindicato.intelligence.event.application;

public class EventNotFoundException extends RuntimeException {

    public EventNotFoundException(Long eventId) {
        super("event not found: " + eventId);
    }
}