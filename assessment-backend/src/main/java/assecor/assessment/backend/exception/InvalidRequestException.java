package assecor.assessment.backend.exception;

public class InvalidRequestException extends RuntimeException {
    public InvalidRequestException(String msg) {
        super(msg);
    }
}
