package agp.andwhat5.exceptions;

@SuppressWarnings("WeakerAccess")
public class AGPException extends Exception {

    String message;

    public AGPException(String exception) {
        message = exception;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
