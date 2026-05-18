package common;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface remota RMI. O cliente invoca {@link #processRequest(byte[])} passando
 * uma mensagem REQUEST empacotada; o servidor devolve uma mensagem REPLY.
 */
public interface RemoteService extends Remote {

    byte[] processRequest(byte[] requestFrame) throws RemoteException;
}
