package ru.practicum.evm.main.exception;

public class IncorrectEventStateException extends RuntimeException {
    public IncorrectEventStateException(String message) {
        super(message);
    }
}
