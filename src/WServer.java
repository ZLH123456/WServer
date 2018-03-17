import java.net.*;
import java.io.*;

//Try-With-Resources

// Url (InputStream) --> go to file --> Download (OutputStream)
// GZIP then ZIP

public class WServer {

    public static void main (String[] args){

        ServerSocket serverSocket;

        try{
            serverSocket = new ServerSocket(8302);

            while(true)
            {
                Concurrency socket_thread = new Concurrency(serverSocket.accept());
                socket_thread.start();
            }

        } catch(IOException IOerr){
            IOerr.printStackTrace();
        }
    }
}