import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class SStorage implements SStorageInterface {

	

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


}
