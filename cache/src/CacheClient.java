import java.io.*;
import java.net.Socket;


public class CacheClient {
    final static String host = "localhost";
    final static int portNumber = 8011;
    Socket socket;
    ObjectInputStream ois;
    ObjectOutputStream oos;

    public CacheClient(){
        System.out.println("Creating socket to '" + host + "' on port " + portNumber);
        try {
            socket = new Socket(host, portNumber);
            ois = new ObjectInputStream(new DataInputStream(socket.getInputStream()));
            oos = new ObjectOutputStream(new DataOutputStream(socket.getOutputStream()));
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }


    public static Packet read(ObjectInputStream ois) throws IOException, ClassNotFoundException{//Only call when you expect an answer
        return (Packet) ois.readObject();
    }
    public static void command(PrintWriter out, String string){
        out.println(string);
    }
    public void send(Packet packet) throws  IOException{
        oos.writeObject(packet);
    }
}
