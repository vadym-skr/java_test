package com.example.test_job_task.exception;

public enum InternalViolationType {

    // Server exceptions or unexpected
    UNEXPECTED_EXCEPTION(1000, "Occurred unexpected exceptions"),

    // Ticket exceptions
    TICKET_WITH_SUCH_ID_NOT_FOUND(1100, "Ticket with such id not found"),
    TICKET_WITH_SUCH_ID_RESERVED(1101, "Ticket with such id is already reserved"),
    TICKET_WITH_SUCH_ID_NOT_RESERVED(1101, "Ticket with such id isn't reserved yet"),

    // Baggage exceptions
    BAGGAGE_WITH_SUCH_ID_NOT_FOUND(1200, "Baggage with such id not found"),

    // Destination exceptions
    DESTINATION_WITH_SUCH_ID_NOT_FOUND(1300, "Destination with such id not found"),

    // Coupon exceptions
    COUPON_WITH_SUCH_ID_NOT_FOUND(1400, "coupon with such id not found");


    private final int code;
    private final String message;

    InternalViolationType(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "ViolationType{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}
