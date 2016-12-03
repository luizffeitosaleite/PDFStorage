import java.rmi.Remote;
import java.rmi.RemoteException;

public interface SStorageInterface extends Remote{

	public boolean receivePDF(String fileName, byte[] data, int len) throws RemoteException;
	
	public boolean sendPDF(CStorageInterface s, String name) throws RemoteException;
	
	public String getPDFList () throws RemoteException;
}
