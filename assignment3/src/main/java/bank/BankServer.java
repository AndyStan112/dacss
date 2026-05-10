package bank;

import RequestReply.ByteStreamTransformer;
import RequestReply.Replier;
import custom_rmi.RemoteInvocationTransformer;
import custom_rmi.RemoteObjectRegistry;

public class BankServer {

    public static void main(String[] args) throws Exception {
        RemoteObjectRegistry registry = new RemoteObjectRegistry();

        BankService bankService = new BankServiceImpl();

        registry.bind("BankService", bankService);

        ByteStreamTransformer transformer = new RemoteInvocationTransformer(registry);

        Replier replier = new Replier("BankServer", 9001, 10);

        System.out.println("Bank custom RMI server started on port 9001");

        replier.start(transformer);
    }
}