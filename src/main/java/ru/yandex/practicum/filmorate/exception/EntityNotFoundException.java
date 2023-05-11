package ru.yandex.practicum.filmorate.exception;

public class EntityNotFoundException extends RuntimeException {
    private final String entityName;

    public EntityNotFoundException(String message, String entityName) {
        super(message);
        this.entityName = entityName;
    }

    public String getEntityName() {
        return entityName;
    }
}
