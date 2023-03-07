import java.io.*;
import java.net.*;
import java.util.*;

public class MyRunnable implements Runnable{
    private Socket connection;
    private ByteArrayOutputStream reply;

    private InputStream in;
    private OutputStream out;

    private String hostname = null;
    private int portNumber = 0;
    private String string = " ";
    private boolean shutdown = false;
    private Integer limit = null;
    private Integer timeout = null;

    public MyRunnable(Socket connection){
        this.connection = connection;
        this.reply = new ByteArrayOutputStream();
    }

    public void run(){ 
        try{
            in = connection.getInputStream();
            out = connection.getOutputStream();
            
            int next;
            while((next = in.read()) != -1){
                reply.write(next);
                if(reply.toString().endsWith("\n")){
                    break;
                }
            }
            String stringReply = reply.toString();

            if(!stringReply.startsWith("GET") || !stringReply.contains("HTTP")){
                out.write("HTTP/1.1 400 Bad Request\r\n".getBytes());
                out.write("Content-Type: text/plain\r\n\r\n".getBytes());
                out.write("Invalid request".getBytes()); 
            }
            else if(!stringReply.contains("/ask")){
                out.write("HTTP/1.1 404 Not Found\r\n".getBytes());
                out.write("Content-Type: text/plain\r\n\r\n".getBytes());
                out.write("Not found".getBytes());
            }
            else{
                try{
                    String url = stringReply.split(" ")[1];
                    String query = url.split("\\?")[1];
                    String[] param = query.split("&");
    
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
                    byte[] serverReply = client.askServer(hostname, portNumber, string.getBytes());
                    out.write("HTTP/1.1 200 OK\r\n".getBytes());
                    out.write("Content-Type: text/plain\r\n\r\n".getBytes());
                    out.write(serverReply);
                }

                catch (Exception e){
                    out.write("HTTP/1.1 400 Bad Request\r\n".getBytes());
                    out.write("Content-Type: text/plain\r\n\r\n".getBytes());
                    out.write("Invalid request".getBytes());
                }
            }
        }
        catch(Exception e){}
        finally{
            try{
                connection.close();
            }
            catch(IOException e){}
        }
    }
}
