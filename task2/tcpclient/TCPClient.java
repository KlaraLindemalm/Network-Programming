package tcpclient;
import java.net.*;
import java.io.*;

public class TCPClient {
boolean shutdown;
Integer timeout;
Integer limit;
    
    public TCPClient(boolean shutdown, Integer timeout, Integer limit) {
    	this.shutdown = shutdown;
    	this.timeout = timeout;
    	this.limit = limit;
    }
    
    public TCPClient(){
    
    }

    public byte[] askServer(String hostname, int port, byte [] toServerBytes) throws IOException {
    	Socket socket = new Socket(hostname, port);
    	InputStream in = socket.getInputStream();
    	OutputStream out = socket.getOutputStream();
    	ByteArrayOutputStream output = new ByteArrayOutputStream();
    	
    	out.write(toServerBytes);
    	
    	if (timeout != null){
    		socket.setSoTimeout(timeout);
    	}
    	
    	if(shutdown == true){
    		socket.shutdownOutput();
    	}
    	
    	try{
    		int i = in.read();
    		while(i != -1 && (limit == null || output.size() < limit)){
    			output.write(i);
    			i = in.read();
    		}
    	}	
     		
     	catch(SocketTimeoutException ex){
     	}
     		
     	socket.close();
     	return output.toByteArray();
    }
    
    public byte[] askServer(String hostname, int port) throws IOException{
    	return askServer(hostname, port, null);
    }
}
