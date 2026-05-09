package course_examples.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

// Remote interface
public interface MathService extends Remote {
    int add(int a, int b) throws RemoteException;
    int mult(int a, int b) throws RemoteException;
}