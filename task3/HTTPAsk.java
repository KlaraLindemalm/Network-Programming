
import java.io.*;
import java.net.*;
import java.util.*;

public class HTTPAsk {
    public static byte[] perReq(String req) throws Exception{
        String url = req.split(" ")[1];
        String query = url.split("\\?")[1];
        String[] param = query.split("&");

        String hostname = null;
        int portNumber = 0;
        String string = " ";
        boolean shutdown = false;
        Integer limit = null;
        Integer timeout = null;

        for (String i : param){
            String[] values = i.split("=");
            switch(values[0]){
                case "hostname":
                    hostname = values[1];
                    break;
                case "port":
                    portNumber = Integer.parseInt(values[1]);
                    break;
                case "string":
                    string = values[1];
                    break;
                case "shutdown":
                    shutdown = Boolean.parseBoolean(values[1]);
                    break;
                case "limit":
                    limit = Integer.parseInt(values[1]);
                    break;
                case "timeout":
                    timeout = Integer.parseInt(values[1]);
                    break;
            }
        }
        TCPClient client = new TCPClient(shutdown, timeout, limit);
        return client.askServer(hostname, portNumber, string.getBytes());
    }
    public static void main(String[] args) throws Exception {
        int port = Integer.parseInt(args[0]);
        ServerSocket serverSocket = new ServerSocket(port);
        //System.out.println("Listening on port " + port);

        while (true) {
            try{
                Socket connection = serverSocket.accept();
                ByteArrayOutputStream reply= new ByteArrayOutputStream();
                InputStream in = connection.getInputStream();
                OutputStream out = connection.getOutputStream();
                DataOutputStream serverOut = new DataOutputStream(out);

                int next;
                while((next = in.read()) != -1){
                    reply.write(next);;
                    if(reply.toString().endsWith("\n")){
                        break;
                    }
                }
                
                String stringReply = reply.toString();
                
                if(!stringReply.startsWith("GET") || !stringReply.contains("HTTP")){
                    serverOut.writeBytes("HTTP/1.1 400 Bad Request\r\n");
                    serverOut.writeBytes("Content-Type: text/plain\r\n\r\n");
                    serverOut.writeBytes("Invalid request"); 
                    connection.close();
                    continue;
                }
                if(!stringReply.contains("/ask")){
                    serverOut.writeBytes("HTTP/1.1 404 Not Found\r\n");
                    serverOut.writeBytes("Content-Type: text/plain\r\n\r\n");
                    serverOut.writeBytes("Not found");
                    connection.close();
                    continue;
                }
                try{
                    byte[] serverReply = perReq(stringReply);
                    System.out.println(new String(serverReply));
                    serverOut.writeBytes("HTTP/1.1 200 OK\r\n");
                    serverOut.writeBytes("Content-Type: text/plain\r\n\r\n");
                    serverOut.writeBytes(new String(serverReply));
                }
                catch (Exception e){
                    serverOut.writeBytes("HTTP/1.1 400 Bad Request\r\n");
                    serverOut.writeBytes("Content-Type: text/plain\r\n\r\n");
                    serverOut.writeBytes("Invalid request");    
                }
                connection.close();
            }
            catch(Exception e){}
        }    
    }
}
