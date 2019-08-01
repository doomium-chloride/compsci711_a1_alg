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
            System.out.println("in constructor try");
            socket = new Socket(host, portNumber);
            System.out.println("socket created");
            oos = new ObjectOutputStream(new DataOutputStream(socket.getOutputStream()));
            System.out.println("oos created");
            ois = new ObjectInputStream(new DataInputStream(socket.getInputStream()));
            System.out.println("ois created");
        } catch (Exception e){
            throw new RuntimeException(e);
        }
        System.out.println("Cache client initialised, port: " + portNumber);
    }


    public Packet read() throws IOException, ClassNotFoundException{//Only call when you expect an answer
        return (Packet) ois.readObject();
    }

    public void send(Packet packet) throws  IOException{
        oos.writeObject(packet);
    }
}
