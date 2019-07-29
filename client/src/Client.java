import java.io.*;
import java.net.Socket;

public class Client {
    final static String host = "localhost";
    final static int portNumber = 8010;
    static Socket socket;
    public static void init(){
        try {
            socket = new Socket(host, portNumber);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }
        public static Packet connect(String command) throws IOException, ClassNotFoundException {

        System.out.println("Creating socket to '" + host + "' on port " + portNumber);
        try(
                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                ObjectInputStream ois =
                        new ObjectInputStream(new DataInputStream(socket.getInputStream()));
                ObjectOutputStream oos =
                        new ObjectOutputStream(new DataOutputStream(socket.getOutputStream()))
        ){

            oos.writeObject(Packet.text(command));
            return (Packet) ois.readObject();
        }

    }

    public static void main(String[] args) {
        init();
        while(true){
            String input = Keyboard.readInput();
            if ("exit".equalsIgnoreCase(input)){
                break;
            }
            try {
                connect(input);
            } catch (Exception e){
                throw new RuntimeException(e);
            }
        }
    }

}
