package srimalar.core.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("errors")
public class ErrorArray {
    private ErrorDetails[] errors;

    public void setErrors(ErrorDetails[] errors) {
        this.errors = errors;
    }

    public ErrorDetails[] getErrors() {
        return errors;
    }

    @JsonIgnore
    public static ErrorArray get(String message) {
        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setDetail(message);
        ErrorArray errorArray = new ErrorArray();
        errorArray.setErrors(new ErrorDetails[]{errorDetails});
        return errorArray;
    }

    @JsonIgnore
    public static ErrorArray getMessage(String code, String details) {
        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setCode(code);
        errorDetails.setDetail(details);
        ErrorArray errorArray = new ErrorArray();
        errorArray.setErrors(new ErrorDetails[]{errorDetails});
        return errorArray;
    }
}
