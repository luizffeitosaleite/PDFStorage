import java.rmi.Remote;
import java.rmi.RemoteException;

public interface SStorageInterface extends Remote{

	public boolean receivePDF(String fileName, byte[] data, int len) throws RemoteException;
	
	public boolean receiveByServer(String fileName, byte[] data, int len) throws RemoteException;
	
	public boolean sendPDF(CStorageInterface s, String name) throws RemoteException;
	
	public boolean deletePDF(String name) throws RemoteException;
	
	public boolean deleteByServer(String name) throws RemoteException;
	
	public boolean addClient() throws RemoteException;

	public boolean removeClient() throws RemoteException;

	public Integer getClient() throws RemoteException;
	
	public String getPDFList () throws RemoteException;
	
	public void syncServer() throws RemoteException;
}
