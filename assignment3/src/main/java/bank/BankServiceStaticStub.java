package bank;

import RequestReply.Requestor;
import custom_rmi.Marshaller;
import custom_rmi.RemoteCallRequest;
import custom_rmi.RemoteCallResponse;
import custom_rmi.RemoteException;

import java.util.Arrays;

public class BankServiceStaticStub implements BankService {

    private final String host;
    private final int port;
    private final String remoteObjectName;

    public BankServiceStaticStub(String host, int port, String remoteObjectName) {
        this.host = host;
        this.port = port;
        this.remoteObjectName = remoteObjectName;
    }

    @Override
    public Integer addAmount(String accountId, Integer amount) {
        Object result = remoteCall("addAmount", accountId, amount);
        return (Integer) result;
    }

    @Override
    public Integer withdrawAmount(String accountId, Integer amount) {
        Object result = remoteCall("withdrawAmount", accountId, amount);
        return (Integer) result;
    }

    @Override
    public String transferAmount(String fromAccountId, String toAccountId, Integer amount) {
        Object result = remoteCall("transferAmount", fromAccountId, toAccountId, amount);
        return (String) result;
    }

    @Override
    public Integer getBalance(String accountId) {
        Object result = remoteCall("getBalance", accountId);
        return (Integer) result;
    }

    private Object remoteCall(String methodName, Object... args) {
        RemoteCallRequest request = new RemoteCallRequest(
                remoteObjectName,
                methodName,
                Arrays.asList(args)
        );

        byte[] requestBytes = Marshaller.marshalRequest(request);

        Requestor requestor = new Requestor("StaticStub");
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
}