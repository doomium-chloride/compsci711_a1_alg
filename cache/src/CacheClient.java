import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

public class CacheClient {
    final static String host = "localhost";
    final static int portNumber = 8011;
    static Socket socket;
    public static void init(){
        try {
            socket = new Socket(host, portNumber);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }
    public static byte[] getBytes(String command) throws IOException{
        DataInputStream dataInputStream = connect(command);
        byte[] input = read(dataInputStream);
        socket.close();
        return input;
    }
    public static File[] getFiles(String command) throws IOException{
        DataInputStream dataInputStream = connect(command);
        try(ObjectInputStream objectInput = new ObjectInputStream(new BufferedInputStream(dataInputStream))){
            return (File[]) objectInput.readObject();
        } catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    public static String getString(String command) throws IOException{
        DataInputStream input = connect(command);
        try(ObjectInputStream objectInput = new ObjectInputStream(input)){
            String str = (String) objectInput.readObject();
            socket.close();
            return str;
        } catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    public static DataInputStream connect(String command) throws IOException {

        System.out.println("Creating socket to '" + host + "' on port " + portNumber);
        try(
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        ){
            command(out, command);
            return dataInputStream;
        }

        //byte[] input2 = read(dataInputStream);



    }

    public static byte[] read(DataInputStream input){
        try(ObjectInputStream objectInput = new ObjectInputStream(new BufferedInputStream(input))){
            List<List<byte[]>> pack = (List<List<byte[]>>) objectInput.readObject();
            return Builder.assemble(pack);
        } catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    public static void command(PrintWriter out, String string){
        out.println(string);
    }
}
