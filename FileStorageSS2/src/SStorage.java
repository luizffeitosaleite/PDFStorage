import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

public class SStorage implements SStorageInterface {

	Integer clients = 0;

	public boolean receiveByServer(String fileName, byte[] data, int len) throws RemoteException {
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

			stub2.receiveByServer(fileName, data, len);
		} catch (RemoteException | NotBoundException e1) {

			try {
				boolean needWrite = true;

				File inputFile = new File("history.txt");
				File tempFile = new File("tempf.txt");

				BufferedReader reader = new BufferedReader(new FileReader(inputFile));
				BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

				String lineToRemove = "remove_"+fileName;
				String currentLine;

				while((currentLine = reader.readLine()) != null) {
					String trimmedLine = currentLine.trim();
					if(trimmedLine.equals(lineToRemove)){
						needWrite = false;
						continue;
					}
					writer.write(currentLine + System.getProperty("line.separator"));
				}

				writer.close();
				reader.close();

				Files.delete(Paths.get("history.txt"));
				tempFile.renameTo(inputFile);

				if(needWrite){
					Writer arq = new BufferedWriter(new FileWriter("history.txt", true));
					arq.append("add_"+fileName+"\n");
					arq.close();
				}
			} catch (IOException e) {
				System.out.println("Failed to save historic");
			}
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

	public boolean sendToServer(SStorageInterface s, String name) throws RemoteException{
		try{
			File file = new File(name);
			FileInputStream in = new FileInputStream(file);

			byte[] mydata = new byte[1024*1024];
			int mylen = in.read(mydata);

			while(mylen>0){
				s.receiveByServer(file.getName(), mydata, mylen);
				mylen = in.read(mydata);
			}

		} catch(Exception e){
			e.printStackTrace();
		}
		return true;
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

	public boolean deleteByServer(String name) throws RemoteException{
		try{
			Files.delete(Paths.get(name));
		} catch (NoSuchFileException e){
			System.out.println("No such file "+ name);
		} catch (Exception e){
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

			stub2.deleteByServer(name);
		} catch (RemoteException | NotBoundException e1) {
			e1.printStackTrace();

			try {
				boolean needWrite = true;

				File inputFile = new File("history.txt");
				File tempFile = new File("tempf.txt");

				BufferedReader reader = new BufferedReader(new FileReader(inputFile));
				BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

				String lineToRemove = "add_"+name;
				String currentLine;

				while((currentLine = reader.readLine()) != null) {
					String trimmedLine = currentLine.trim();
					if(trimmedLine.equals(lineToRemove)){
						needWrite = false;
						continue;
					}
					writer.write(currentLine + System.getProperty("line.separator"));
				}

				writer.close();
				reader.close();

				Files.delete(Paths.get("history.txt"));
				tempFile.renameTo(inputFile);

				if(needWrite){
					Writer arq = new BufferedWriter(new FileWriter("history.txt", true));
					arq.append("remove_"+name+"\n");
					arq.close();
				}
			} catch (IOException e) {
				System.out.println("Failed to save historic");
			}
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

	public void syncServer() throws RemoteException {
		try{
			Registry r2 = LocateRegistry.getRegistry("localhost");
			SStorageInterface stub2 = (SStorageInterface) r2.lookup("Storage_1");

			stub2.syncServer();
			
			File inputFile = new File("history.txt");
			File tempFile = new File("tempf.txt");

			BufferedReader reader = new BufferedReader(new FileReader(inputFile));
			BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

			String currentLine;

			while((currentLine = reader.readLine()) != null) {
				String trimmedLine = currentLine.trim();
				if(trimmedLine.contains("add_")){
					sendToServer(stub2, trimmedLine.replaceAll("add_", ""));
					continue;
				} else{
					if(trimmedLine.contains("remove_")){
						stub2.deleteByServer(trimmedLine.replaceAll("remove_", ""));
						continue;
					}
				}
				writer.write(currentLine + System.getProperty("line.separator"));
			}

			writer.close();
			reader.close();

			Files.delete(Paths.get("history.txt"));
			tempFile.renameTo(inputFile);

		} catch (Exception e){
			e.printStackTrace();
		}
	}


}
