package com.example.test_job_task.exception;

public class InternalViolationException extends RuntimeException {

    private final InternalViolationType type;
    private final String customMessage;

    public InternalViolationException(InternalViolationType type) {
        super(type.getMessage(), null, false, false);
        this.type = type;
        this.customMessage = type.getMessage();
    }

    public InternalViolationException(InternalViolationType type, String customMessage) {
        super(customMessage, null, false, false);
        this.type = type;
        this.customMessage = customMessage;
    }

    public InternalViolationType getType() {
        return type;
    }

    public String getCustomMessage() {
        return customMessage;
    }
}
