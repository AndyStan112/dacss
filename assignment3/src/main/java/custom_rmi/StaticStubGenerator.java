package custom_rmi;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class StaticStubGenerator {

    public static void generateStub(String interfaceName) {
        try {
            Class<?> serviceInterface = Class.forName(interfaceName);

            if (!serviceInterface.isInterface()) {
                throw new RemoteException(interfaceName + " is not an interface");
            }

            validateInterface(serviceInterface);

            String packageName = serviceInterface.getPackageName();
            String interfaceSimpleName = serviceInterface.getSimpleName();
            String stubSimpleName = interfaceSimpleName + "Stub";

            String sourceCode = generateSourceCode(
                    packageName,
                    interfaceSimpleName,
                    stubSimpleName,
                    serviceInterface
            );

            File outputFile = getOutputFile(packageName, stubSimpleName);

            File parent = outputFile.getParentFile();
            if (!parent.exists() && !parent.mkdirs()) {
                throw new RemoteException(
                        "Could not create output directory: " + parent.getAbsolutePath()
                );
            }

            try (FileWriter writer = new FileWriter(outputFile)) {
                writer.write(sourceCode);
            }

            System.out.println("Generated static stub:");
            System.out.println(outputFile.getAbsolutePath());

        } catch (ClassNotFoundException e) {
            throw new RemoteException("Interface not found locally: " + interfaceName, e);
        } catch (IOException e) {
            throw new RemoteException("Could not write static stub source file", e);
        }
    }

    private static File getOutputFile(String packageName, String stubSimpleName) {
        String packagePath = packageName.replace(".", File.separator);

        return new File(
                "src/main/java/" + packagePath + "/" + stubSimpleName + ".java"
        );
    }

    private static void validateInterface(Class<?> serviceInterface) {
        for (Method method : serviceInterface.getMethods()) {
            validateMethod(method);
        }
    }

    private static String generateSourceCode(
            String packageName,
            String interfaceSimpleName,
            String stubSimpleName,
            Class<?> serviceInterface
    ) {
        StringBuilder code = new StringBuilder();

        code.append("package ").append(packageName).append(";\n\n");

        code.append("import RequestReply.Requestor;\n");
        code.append("import custom_rmi.Marshaller;\n");
        code.append("import custom_rmi.RemoteCallRequest;\n");
        code.append("import custom_rmi.RemoteCallResponse;\n");
        code.append("import custom_rmi.RemoteException;\n");
        code.append("import java.util.Arrays;\n\n");

        code.append("public class ").append(stubSimpleName)
                .append(" implements ").append(interfaceSimpleName).append(" {\n\n");

        code.append("    private final String host;\n");
        code.append("    private final int port;\n");
        code.append("    private final String remoteObjectName;\n\n");

        code.append("    public ").append(stubSimpleName)
                .append("(String host, int port, String remoteObjectName) {\n");
        code.append("        this.host = host;\n");
        code.append("        this.port = port;\n");
        code.append("        this.remoteObjectName = remoteObjectName;\n");
        code.append("    }\n\n");

        for (Method method : serviceInterface.getMethods()) {
            code.append(generateMethod(method));
        }

        code.append(generateRemoteCallMethod());

        code.append("}\n");

        return code.toString();
    }

    private static String generateMethod(Method method) {
        StringBuilder code = new StringBuilder();

        Class<?> returnType = method.getReturnType();
        Parameter[] parameters = method.getParameters();

        code.append("    @Override\n");
        code.append("    public ")
                .append(toJavaTypeName(returnType))
                .append(" ")
                .append(method.getName())
                .append("(");

        for (int i = 0; i < parameters.length; i++) {
            if (i > 0) {
                code.append(", ");
            }

            Class<?> parameterType = parameters[i].getType();

            code.append(toJavaTypeName(parameterType))
                    .append(" arg")
                    .append(i);
        }

        code.append(") {\n");

        code.append("        Object result = remoteCall(\"")
                .append(method.getName())
                .append("\"");

        for (int i = 0; i < parameters.length; i++) {
            code.append(", arg").append(i);
        }

        code.append(");\n");

        code.append("        return (")
                .append(toJavaTypeName(returnType))
                .append(") result;\n");

        code.append("    }\n\n");

        return code.toString();
    }

    private static String generateRemoteCallMethod() {
        return """
            private Object remoteCall(String methodName, Object... args) {
                RemoteCallRequest request = new RemoteCallRequest(
                        remoteObjectName,
                        methodName,
                        Arrays.asList(args)
                );

                byte[] requestBytes = Marshaller.marshalRequest(request);

                Requestor requestor = new Requestor("StaticClientStub");
                byte[] responseBytes =
                        requestor.sendRequestAndWaitResponse(host, port, requestBytes);

                if (responseBytes == null) {
                    throw new RemoteException("No response from server");
                }

                RemoteCallResponse response = Marshaller.unmarshalResponse(responseBytes);

                if (!response.isSuccess()) {
                    throw new RemoteException(response.getErrorMessage());
                }

                return response.getResult();
            }

        """;
    }

    private static void validateMethod(Method method) {
        Class<?> returnType = method.getReturnType();

        if (!isSupportedType(returnType)) {
            throw new RemoteException(
                    "Unsupported return type in method "
                            + method.getName()
                            + ": "
                            + returnType.getName()
            );
        }

        for (Class<?> parameterType : method.getParameterTypes()) {
            if (!isSupportedType(parameterType)) {
                throw new RemoteException(
                        "Unsupported parameter type in method "
                                + method.getName()
                                + ": "
                                + parameterType.getName()
                );
            }
        }
    }

    private static boolean isSupportedType(Class<?> type) {
        return type == Integer.class
                || type == int.class
                || type == String.class;
    }

    private static String toJavaTypeName(Class<?> type) {
        if (type == Integer.class) {
            return "Integer";
        }

        if (type == int.class) {
            return "int";
        }

        if (type == String.class) {
            return "String";
        }

        throw new RemoteException("Unsupported Java type: " + type.getName());
    }
}