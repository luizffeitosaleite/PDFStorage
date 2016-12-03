import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class CStorage extends UnicastRemoteObject implements CStorageInterface{

	private String name = "";
	
	protected CStorage() throws RemoteException{}
	
	public void setFileName(String f){
		name = f;
	}
	
	public boolean uploadPDF(SStorageInterface s) throws RemoteException {
		
		try{
			File file = new File(name);
			FileInputStream in = new FileInputStream(file);
			
			byte [] mydata = new byte[1024*1024];
			int mylen = in.read(mydata);
			
			while(mylen>0){
				s.receivePDF(file.getName(), mydata, mylen);
				mylen = in.read(mydata);
			}
			
		} catch(Exception e){
			e.printStackTrace();
		}
		return true;
	}

	public boolean downloadPDF(String fileName, byte[] data, int len) throws RemoteException {
		try{
			File f = new File(fileName);
			f.createNewFile();
			FileOutputStream out = new FileOutputStream(f, true);
			out.write(data, 0, len);
			out.flush();
			out.close();
			System.out.println("Done!");
		} catch(Exception e){
			e.printStackTrace();
		}
		return true;
	}

	

}
