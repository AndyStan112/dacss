package custom_rmi;

public class RemoteCallResponse {

    private final boolean success;
    private final Object result;
    private final String errorMessage;

    private RemoteCallResponse(boolean success, Object result, String errorMessage) {
        this.success = success;
        this.result = result;
        this.errorMessage = errorMessage;
    }

    public static RemoteCallResponse success(Object result) {
        return new RemoteCallResponse(true, result, null);
    }

    public static RemoteCallResponse error(String errorMessage) {
        return new RemoteCallResponse(false, null, errorMessage);
    }

    public boolean isSuccess() {
        return success;
    }

    public Object getResult() {
        return result;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}