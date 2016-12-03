import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class Client {

	private Client(){}

	public static void main(String[] args) {
		String host = (args.length < 1) ? null : args[0];

		try{
			CStorage cs = new CStorage();
			//registry for server 1
			Registry registry1 = LocateRegistry.getRegistry(host);

			//registry for server 2
			Registry registry2 = LocateRegistry.getRegistry(host, 2001);
			
			SStorageInterface stub1 = (SStorageInterface) registry1.lookup("Storage_1");
			
			SStorageInterface stub2 = (SStorageInterface) registry2.lookup("Storage_2");
			
			Scanner sc = new Scanner(System.in);
			boolean stop = false;

			//TODO replicar comandos para servidor 2
			do{
				System.out.println("Upload PDF [1] \nList PDFs [2] \nDownload PDF [3] \nDelete PDF [4] \nExit [5]\n");
				Integer input = sc.nextInt();
				sc.nextLine();
				switch (input){
				case 1:
					System.out.println("Enter the file name: ");
					cs.setFileName(sc.nextLine());
					cs.uploadPDF(stub1);
					break;
				case 2:
					System.out.println(stub1.getPDFList());
					break;
				case 3:
					System.out.println("Enter the file name: ");
					stub1.sendPDF(cs, sc.nextLine());
					break;
				case 4:
					System.out.println("Enter the file name: ");
					stub1.deletePDF(sc.nextLine());
					break;
				case 5:
					System.out.println("Bye!");
					stop = true;
					break;
				default:
					System.out.println("Invalid option\n");
					break;
				}
			}while(!stop);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
}
