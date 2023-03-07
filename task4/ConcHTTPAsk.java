import java.net.*;
import java.io.*;
import java.util.*;
import java.lang.Runnable;

public class ConcHTTPAsk{
    public static void main(String[] args) throws IOException{
        int port = Integer.parseInt(args[0]);
        ServerSocket serverSocket = new ServerSocket(port);
        
        while(true){
            Socket connection = serverSocket.accept();
            MyRunnable runnable = new MyRunnable(connection);
            Thread thread = new Thread(runnable);
            thread.start();
        }
    }
}