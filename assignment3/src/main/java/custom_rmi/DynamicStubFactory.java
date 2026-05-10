package custom_rmi;

import RequestReply.Requestor;

import java.lang.reflect.Proxy;
import java.util.Arrays;

public class DynamicStubFactory {

    @SuppressWarnings("unchecked")
    public static <T> T createStub(
            Class<T> serviceInterface,
            String host,
            int port,
            String remoteObjectName
    ) {
        return (T) Proxy.newProxyInstance(
                serviceInterface.getClassLoader(),
                new Class<?>[]{serviceInterface},
                (proxy, method, args) -> {
                    if (args == null) {
                        args = new Object[0];
                    }

                    RemoteCallRequest request = new RemoteCallRequest(
                            remoteObjectName,
                            method.getName(),
                            Arrays.asList(args)
                    );

                    byte[] requestBytes = Marshaller.marshalRequest(request);

                    Requestor requestor = new Requestor("DynamicStub");
                    byte[] responseBytes = requestor.sendRequestAndWaitResponse(host, port, requestBytes);

                    if (responseBytes == null) {
                        throw new RemoteException("No response from server");
                    }

                    RemoteCallResponse response = Marshaller.unmarshalResponse(responseBytes);

                    if (!response.isSuccess()) {
                        throw new RemoteException(response.getErrorMessage());
                    }

                    return response.getResult();
                }
        );
    }
}