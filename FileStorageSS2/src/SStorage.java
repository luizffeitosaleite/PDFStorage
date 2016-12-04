import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class SStorage implements SStorageInterface {

	Integer clients = 0;

	public boolean receivePDF(String fileName, byte[] data, int len) throws RemoteException {
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

		//try connect server 2
		try {
			Registry r2 = LocateRegistry.getRegistry("localhost");
			SStorageInterface stub2 = (SStorageInterface) r2.lookup("Storage_1");

			stub2.receivePDF(fileName, data, len);
		} catch (NotBoundException e1) {
			e1.printStackTrace();
		}
		return true;
	}

	public String getPDFList() throws RemoteException {

		File dir = new File(".\\");

		File[] fList = dir.listFiles();
		String nList = "";

		for(File file : fList){
			if(file.getName().endsWith(".pdf")){
				nList = nList + file.getName()+"\n";
			}
		}

		if(nList == ""){
			nList = "Empty";
		}
		return nList;
	}

	public boolean sendPDF(CStorageInterface s, String name) throws RemoteException {

		try{
			File file = new File(name);
			FileInputStream in = new FileInputStream(file);

			byte[] mydata = new byte[1024*1024];
			int mylen = in.read(mydata);

			while(mylen>0){
				s.downloadPDF(file.getName(), mydata, mylen);
				mylen = in.read(mydata);
			}

		} catch(Exception e){
			e.printStackTrace();
		}
		return true;
	}

	public boolean deletePDF(String name) throws RemoteException {
		try{
			Files.delete(Paths.get(name));
		} catch (NoSuchFileException e){
			System.out.println("No such file "+ name);
		} catch (Exception e){
			e.printStackTrace();
		}

		//try connect server 2
		try {
			Registry r2 = LocateRegistry.getRegistry("localhost");
			SStorageInterface stub2 = (SStorageInterface) r2.lookup("Storage_1");

			stub2.deletePDF(name);
		} catch (NotBoundException e1) {
			e1.printStackTrace();
		}
		return true;
	}

	public boolean addClient() throws RemoteException {
		clients++;
		return false;
	}

	public boolean removeClient() throws RemoteException {
		clients--;
		return false;
	}

	public Integer getClient() throws RemoteException {
		return clients;
	}

}
