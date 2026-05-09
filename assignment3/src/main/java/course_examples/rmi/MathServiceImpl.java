package course_examples.rmi;

import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;

// Implementation of the remote interface
public class MathServiceImpl extends UnicastRemoteObject implements MathService {

    protected MathServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public int add(int a, int b) throws RemoteException {
        return a + b;
    }

    @Override
    public int mult(int a, int b) throws RemoteException {
        return a * b;
    }
}