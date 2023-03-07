package tcpclient;
import java.net.*;
import java.io.*;

public class TCPClient {
    
    public TCPClient() {
    }

    public byte[] askServer(String hostname, int port, byte [] toServerBytes) throws IOException {
    	try (	Socket socket = new Socket(hostname, port);
    			OutputStream out = socket.getOutputStream();
    			InputStream in = socket.getInputStream();
    			ByteArrayOutputStream output = new ByteArrayOutputStream();){
    			
    			out.write(toServerBytes);
    			
    			int i ;
    			while((i = in.read()) != -1){
    				output.write(i);
    			}
    			
    			return output.toByteArray();
     		}
     		catch(IOException ex){
     			throw ex;
     		}
    }
    
    public byte[] askServer(String hostname, int port) throws IOException{
    	return askServer(hostname, port, null);
    }
}
