package bif3.swe1.rest.enums;

public enum responseCodes {
    OK("HTTP/1.1 200 OK\r\n\r\n"),
    CLIENT_ERROR("HTTP/1.1 400 Wrong Input\r\n\r\n"),
    UNAUTHORIZED_ERROR("HTTP/1.1 401 Unauthorized\r\n\r\n"),
    SERVER_ERROR("HTTP/1.1 500 Server Error\r\n\r\n");
    private String errorCode;

    responseCodes(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return this.errorCode;
    }
}
