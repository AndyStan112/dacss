package custom_rmi;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Marshaller {

    private static final String TYPE_NULL = "NULL";
    private static final String TYPE_INTEGER = "INTEGER";
    private static final String TYPE_STRING = "STRING";

    public static byte[] marshalRequest(RemoteCallRequest request) {
        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(byteOut);

            out.writeUTF(request.getObjectName());
            out.writeUTF(request.getMethodName());

            List<Object> arguments = request.getArguments();
            out.writeInt(arguments.size());

            for (Object argument : arguments) {
                writeValue(out, argument);
            }

            out.flush();
            return byteOut.toByteArray();

        } catch (IOException e) {
            throw new RemoteException("Could not marshal request", e);
        }
    }

    public static RemoteCallRequest unmarshalRequest(byte[] data) {
        try {
            ByteArrayInputStream byteIn = new ByteArrayInputStream(data);
            DataInputStream in = new DataInputStream(byteIn);

            String objectName = in.readUTF();
            String methodName = in.readUTF();

            int argumentCount = in.readInt();
            List<Object> arguments = new ArrayList<>();

            for (int i = 0; i < argumentCount; i++) {
                arguments.add(readValue(in));
            }

            return new RemoteCallRequest(objectName, methodName, arguments);

        } catch (IOException e) {
            throw new RemoteException("Could not unmarshal request", e);
        }
    }

    public static byte[] marshalResponse(RemoteCallResponse response) {
        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(byteOut);

            out.writeBoolean(response.isSuccess());

            if (response.isSuccess()) {
                writeValue(out, response.getResult());
            } else {
                out.writeUTF(
                        response.getErrorMessage() == null
                                ? "Unknown remote error"
                                : response.getErrorMessage()
                );
            }

            out.flush();
            return byteOut.toByteArray();

        } catch (IOException e) {
            throw new RemoteException("Could not marshal response", e);
        }
    }

    public static RemoteCallResponse unmarshalResponse(byte[] data) {
        try {
            ByteArrayInputStream byteIn = new ByteArrayInputStream(data);
            DataInputStream in = new DataInputStream(byteIn);

            boolean success = in.readBoolean();

            if (success) {
                Object result = readValue(in);
                return RemoteCallResponse.success(result);
            } else {
                String errorMessage = in.readUTF();
                return RemoteCallResponse.error(errorMessage);
            }

        } catch (IOException e) {
            throw new RemoteException("Could not unmarshal response", e);
        }
    }

    private static void writeValue(DataOutputStream out, Object value) throws IOException {
        if (value == null) {
            out.writeUTF(TYPE_NULL);
        } else if (value instanceof Integer) {
            out.writeUTF(TYPE_INTEGER);
            out.writeInt((Integer) value);
        } else if (value instanceof String) {
            out.writeUTF(TYPE_STRING);
            out.writeUTF((String) value);
        } else {
            throw new RemoteException("Unsupported type: " + value.getClass().getName());
        }
    }

    private static Object readValue(DataInputStream in) throws IOException {
        String type = in.readUTF();

        switch (type) {
            case TYPE_NULL:
                return null;

            case TYPE_INTEGER:
                return in.readInt();

            case TYPE_STRING:
                return in.readUTF();

            default:
                throw new RemoteException("Unsupported type received: " + type);
        }
    }
}