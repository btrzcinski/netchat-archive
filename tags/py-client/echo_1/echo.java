import java.io.IOException;

import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

public class echo{
	static final String HOST = "agammemnon.csl.tjhsst.edu";
	static final int PORT = 45287;

	public static void main(String[] args){
		Connection c = new Connection(HOST, PORT);
	}
}

class Connection{
	String host;
	int port;
	Socket socket;

	public Connection(String h, int p){
		host = h;
		port = p;
		try{
			socket = new Socket(h, p);
		}
		// TODO: Better error handling
		catch(UnknownHostException uhe){
			// XXX: This is a placeholder
			System.out.printf("Host '%s' not found. Exiting.\n", h);
			System.exit(0);
		}
		catch(ConnectException ce){
			System.out.printf("Connection refused on port %d. Exiting.\n", p);
			System.exit(0);
		}
		catch(IOException ioe){
			System.out.println("IOException caught. Exiting.");
			ioe.printStackTrace();
			System.exit(0);
		}

		System.out.println("Connection established!");
	}
}
