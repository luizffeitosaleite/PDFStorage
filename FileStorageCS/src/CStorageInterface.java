import java.rmi.Remote;
import java.rmi.RemoteException;

public interface CStorageInterface extends Remote{
	public boolean uploadPDF(SStorageInterface s) throws RemoteException;
	
	public boolean downloadPDF(String fileName, byte[] data, int len) throws RemoteException;
	
}

