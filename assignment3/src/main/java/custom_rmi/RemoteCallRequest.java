package custom_rmi;

import java.util.List;

public class RemoteCallRequest {

    private final String objectName;
    private final String methodName;
    private final List<Object> arguments;

    public RemoteCallRequest(String objectName, String methodName, List<Object> arguments) {
        this.objectName = objectName;
        this.methodName = methodName;
        this.arguments = arguments;
    }

    public String getObjectName() {
        return objectName;
    }

    public String getMethodName() {
        return methodName;
    }

    public List<Object> getArguments() {
        return arguments;
    }
}