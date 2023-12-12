package srimalar.core.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("error")
public class ErrorDetails {
    private String code;
    private String detail;
    private int status;

    public ErrorDetails() {
        this.status = 400;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @JsonIgnore
    public static ErrorDetails get(String details) {
        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setDetail(details);
        return errorDetails;
    }
}

