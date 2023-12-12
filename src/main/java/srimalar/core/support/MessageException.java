package srimalar.core.support;

import srimalar.core.model.ErrorArray;
import srimalar.core.model.ErrorDetails;

public class MessageException extends RuntimeException {
    private final ErrorArray errors;

    public MessageException() {
        super();
        errors = ErrorArray.get("BAD REQUEST");
    }

    public MessageException(String message) {
        super(message);
        errors = ErrorArray.get(message);
    }

    public MessageException(Throwable cause, String message) {
        super(message, cause);
        errors = ErrorArray.get(message);
    }

    public MessageException(Throwable cause) {
        super(cause);
        errors = ErrorArray.get(cause.getMessage());
    }

    public MessageException(ErrorDetails errorDetails) {
        super(errorDetails.getDetail());
        errors = new ErrorArray();
        errors.setErrors(new ErrorDetails[]{errorDetails});
    }

    public ErrorDetails[] getErrors() {
        return errors.getErrors();
    }
}
