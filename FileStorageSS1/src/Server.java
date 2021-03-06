import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Server {

	public Server(){		
	}

	public static void main(String[] args) {

		try{
			SStorage obj = new SStorage();

			SStorageInterface stub = (SStorageInterface) UnicastRemoteObject.exportObject(obj, 0);
			
			Registry registry = LocateRegistry.getRegistry("localhost", 1099);
			registry.bind("Storage_1", stub);
			System.out.println("Server 1 up!");
			obj.syncServer();
		} catch(Exception e){
			e.printStackTrace();
		}
	}
}
