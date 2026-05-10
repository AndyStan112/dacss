package custom_rmi;

import RequestReply.ByteStreamTransformer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class RemoteInvocationTransformer implements ByteStreamTransformer {

    private final RemoteObjectRegistry registry;

    public RemoteInvocationTransformer(RemoteObjectRegistry registry) {
        this.registry = registry;
    }

    @Override
    public byte[] transform(byte[] input) {
        try {
            RemoteCallRequest request = Marshaller.unmarshalRequest(input);

            Object targetObject = registry.lookup(request.getObjectName());

            if (targetObject == null) {
                return Marshaller.marshalResponse(
                        RemoteCallResponse.error("Remote object not found: " + request.getObjectName())
                );
            }

            Object result = invokeMethod(targetObject, request.getMethodName(), request.getArguments());

            return Marshaller.marshalResponse(RemoteCallResponse.success(result));

        } catch (Exception e) {
            return Marshaller.marshalResponse(RemoteCallResponse.error(e.getMessage()));
        }
    }

    private Object invokeMethod(Object targetObject, String methodName, List<Object> arguments)
            throws InvocationTargetException, IllegalAccessException {

        Method method = findMatchingMethod(targetObject, methodName, arguments);

        if (method == null) {
            throw new RemoteException("No matching method found: " + methodName);
        }

        return method.invoke(targetObject, arguments.toArray());
    }

    private Method findMatchingMethod(Object targetObject, String methodName, List<Object> arguments) {
        Method[] methods = targetObject.getClass().getMethods();

        for (Method method : methods) {
            if (!method.getName().equals(methodName)) {
                continue;
            }

            Class<?>[] parameterTypes = method.getParameterTypes();

            if (parameterTypes.length != arguments.size()) {
                continue;
            }

            if (parametersMatch(parameterTypes, arguments)) {
                return method;
            }
        }

        return null;
    }

    private boolean parametersMatch(Class<?>[] parameterTypes, List<Object> arguments) {
        for (int i = 0; i < parameterTypes.length; i++) {
            Object argument = arguments.get(i);
            Class<?> expectedType = parameterTypes[i];

            if (argument == null) {
                continue;
            }

            if (expectedType == int.class && argument instanceof Integer) {
                continue;
            }

            if (expectedType == Integer.class && argument instanceof Integer) {
                continue;
            }

            if (expectedType == String.class && argument instanceof String) {
                continue;
            }

            return false;
        }

        return true;
    }
}