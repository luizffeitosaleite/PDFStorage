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
			Registry registry = LocateRegistry.getRegistry(host);
			SStorageInterface stub = (SStorageInterface) registry.lookup("Storage");

			Scanner sc = new Scanner(System.in);
			boolean stop = false;

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
