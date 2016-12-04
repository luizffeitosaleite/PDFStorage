import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class Client {

	private Client(){}

	//0= not connected; 1 = server_1; 2 = server_2
	Integer server = 0;
	
	public SStorageInterface connectS1(String host, Registry registry, SStorageInterface stub) throws RemoteException, NotBoundException {
		//Servidor 1
		registry = LocateRegistry.getRegistry(host);
		stub = (SStorageInterface) registry.lookup("Storage_1");
		server = 1;
		return stub;
	}

	public SStorageInterface connectS2(String host, Registry registry, SStorageInterface stub) throws RemoteException, NotBoundException {
		//Servidor 2
		registry = LocateRegistry.getRegistry(host, 2001);
		stub = (SStorageInterface) registry.lookup("Storage_2");
		server = 2;
		return stub;
	}

	public SStorageInterface alternateServer(String host, Registry registry, SStorageInterface stub) throws RemoteException, NotBoundException {
		if(server == 1){
			connectS2(host, registry, stub);
		} else{
			connectS1(host, registry, stub);
		}
		return stub;
	}


	public static boolean menu(SStorageInterface stub) throws RemoteException {
		CStorage cs = new CStorage();
		Scanner sc = new Scanner(System.in);
		boolean stop = false;	
		boolean stop_connection = false;

		//Menu
		do{
			System.out.println("Upload PDF [1] \nList PDFs [2] \nDownload PDF [3] \nDelete PDF [4] \nExit [5]\n");
			Integer input = sc.nextInt();
			sc.nextLine();
			switch (input){
			case 1:
				System.out.println("Enter the file name: ");
				cs.setFileName(sc.nextLine());
				cs.uploadPDF(stub);
				break;
			case 2:
				System.out.println(stub.getPDFList());
				break;
			case 3:
				System.out.println("Enter the file name: ");
				stub.sendPDF(cs, sc.nextLine());
				break;
			case 4:
				System.out.println("Enter the file name: ");
				stub.deletePDF(sc.nextLine());
				break;
			case 5:
				System.out.println("Bye!");
				stop = true;
				stub.removeClient();
				stop_connection = true;
				break;
			default:
				System.out.println("Invalid option\n");
				break;
			}
		}while(!stop);

		return stop_connection;
	}

	public static void main(String[] args) {
		String host = (args.length < 1) ? null : args[0];
		Registry registry = null;
		SStorageInterface stub = null;
		Client c = new Client();
		boolean stop = false;
		boolean failed = false;
		Integer clients;
		while(!stop){
			try {
				stub = c.connectS1(host, registry, stub);
				clients = stub.getClient();
				
				try{
					stub = c.connectS2(host, registry, stub);
					if(stub.getClient()>=clients){
						stub = c.connectS1(host, registry, stub);
					}
				} catch(RemoteException | NotBoundException e){
					stub = c.connectS1(host, registry, stub);
				}
				stub.addClient();
				System.out.println("Connected to server "+c.server);
				failed = false;
				stop = menu(stub);
				
			} catch (RemoteException | NotBoundException e) {
				
				failed = true;
				try {
					stub = c.connectS2(host, registry, stub);
					stub.addClient();
					System.out.println("Connected to server "+c.server);
					failed = false;
					stop = menu(stub);
				} catch (RemoteException | NotBoundException e1) {

					if(failed == true){
						stop = true;
					}
				}
			}
		}

	}
}
