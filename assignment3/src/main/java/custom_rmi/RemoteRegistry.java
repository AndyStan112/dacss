package custom_rmi;

import RequestReply.Replier;
import RequestReply.Requestor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RemoteRegistry {

    private final boolean serverMode;
    private final String host;
    private final int port;
    private final StubMode stubMode;

    private final Map<String, Object> objects;
    private final Map<String, String> interfaceNames;

    private RemoteRegistry(
            boolean serverMode,
            String host,
            int port,
            StubMode stubMode
    ) {
        this.serverMode = serverMode;
        this.host = host;
        this.port = port;
        this.stubMode = stubMode;

        if (serverMode) {
            this.objects = new HashMap<>();
            this.interfaceNames = new HashMap<>();
        } else {
            this.objects = null;
            this.interfaceNames = null;
        }
    }

    public static RemoteRegistry createRegistry(int port) {
        RemoteRegistry registry =
                new RemoteRegistry(true, "localhost", port, StubMode.DYNAMIC);

        Thread serverThread = new Thread(() -> {
            try {
                Replier replier = new Replier("RemoteRegistry", port, 10);
                RemoteInvocationTransformer transformer =
                        new RemoteInvocationTransformer(registry);

                replier.start(transformer);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        serverThread.setDaemon(false);
        serverThread.start();

        return registry;
    }

    public static RemoteRegistry getRegistry(String host, int port, StubMode stubMode) {
        return new RemoteRegistry(false, host, port, stubMode);
    }

    public synchronized void rebind(String name, Object remoteObject) {
        if (!serverMode) {
            throw new RemoteException("rebind() can only be called on a server registry");
        }

        objects.put(name, remoteObject);

        Class<?>[] interfaces = remoteObject.getClass().getInterfaces();

        if (interfaces.length == 0) {
            throw new RemoteException(
                    "Remote object must implement at least one interface: "
                            + remoteObject.getClass().getName()
            );
        }

        String interfaceName = interfaces[0].getName();
        interfaceNames.put(name, interfaceName);

        System.out.println("Bound remote object: " + name);
        System.out.println("Interface: " + interfaceName);
    }

    public Object lookup(String name) {
        if (serverMode) {
            throw new RemoteException("lookup() should be called on a client registry");
        }

        String interfaceName = lookupInterfaceNameFromServer(name);

        if (stubMode == StubMode.STATIC) {
            return createStaticStub(name, interfaceName);
        }

        return createDynamicStub(name, interfaceName);
    }

    private Object createStaticStub(String remoteObjectName, String interfaceName) {
        String stubClassName = interfaceName + "Stub";

        try {
            Class<?> stubClass = Class.forName(stubClassName);

            Constructor<?> constructor =
                    stubClass.getConstructor(String.class, int.class, String.class);

            return constructor.newInstance(host, port, remoteObjectName);

        } catch (ClassNotFoundException e) {
            throw new RemoteException(
                    "Static stub class not found: " + stubClassName + "\n" +
                            "Generate it first using:\n" +
                            "  java custom_rmi.StaticStubGeneratorCli " + interfaceName + "\n" +
                            "Then compile the project again.",
                    e
            );

        } catch (NoSuchMethodException e) {
            throw new RemoteException(
                    "Static stub class exists but does not have the required constructor:\n" +
                            "  " + stubClassName + "(String host, int port, String remoteObjectName)",
                    e
            );

        } catch (Exception e) {
            throw new RemoteException(
                    "Could not instantiate static stub: " + stubClassName,
                    e
            );
        }
    }

    private Object createDynamicStub(String remoteObjectName, String interfaceName) {
        try {
            Class<?> serviceInterface = Class.forName(interfaceName);

            return Proxy.newProxyInstance(
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

                        Requestor requestor = new Requestor("DynamicClientStub");
                        byte[] responseBytes =
                                requestor.sendRequestAndWaitResponse(host, port, requestBytes);

                        if (responseBytes == null) {
                            throw new RemoteException("No response from server");
                        }

                        RemoteCallResponse response =
                                Marshaller.unmarshalResponse(responseBytes);

                        if (!response.isSuccess()) {
                            throw new RemoteException(response.getErrorMessage());
                        }

                        return response.getResult();
                    }
            );

        } catch (ClassNotFoundException e) {
            throw new RemoteException(
                    "Client does not have interface class locally: " + interfaceName,
                    e
            );
        }
    }

    private String lookupInterfaceNameFromServer(String name) {
        RemoteCallRequest request = new RemoteCallRequest(
                RemoteInvocationTransformer.REGISTRY_OBJECT,
                RemoteInvocationTransformer.LOOKUP_METHOD,
                Arrays.asList(name)
        );

        byte[] requestBytes = Marshaller.marshalRequest(request);

        Requestor requestor = new Requestor("RegistryClient");
        byte[] responseBytes =
                requestor.sendRequestAndWaitResponse(host, port, requestBytes);

        if (responseBytes == null) {
            throw new RemoteException("No response from registry");
        }

        RemoteCallResponse response = Marshaller.unmarshalResponse(responseBytes);

        if (!response.isSuccess()) {
            throw new RemoteException(response.getErrorMessage());
        }

        return (String) response.getResult();
    }

    synchronized Object lookupObjectLocal(String name) {
        if (!serverMode) {
            throw new RemoteException("Local object lookup is only available on server registry");
        }

        return objects.get(name);
    }

    synchronized String lookupInterfaceNameLocal(String name) {
        if (!serverMode) {
            throw new RemoteException("Local interface lookup is only available on server registry");
        }

        return interfaceNames.get(name);
    }

    public StubMode getStubMode() {
        return stubMode;
    }
}