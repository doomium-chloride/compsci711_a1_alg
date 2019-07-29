import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Relay {
    final static String DIRECTORY = "samples/";
    final static int portNumber = 8010;
    static CacheClient cacheClient;
    public static void main(String[] args) {
        new Relay().connect();
    }

    private void init(){
        cacheClient = new CacheClient();
    }

    public void connect(){
        CacheClient.init();
        init();
        try (
                ServerSocket serverSocket = new ServerSocket(portNumber);
                Socket clientSocket = serverSocket.accept();
                PrintWriter out =
                        new PrintWriter(clientSocket.getOutputStream(), true);
                Scanner in = new Scanner(
                        new InputStreamReader(clientSocket.getInputStream()));
                BufferedInputStream dataIn =
                        new BufferedInputStream(new DataInputStream(clientSocket.getInputStream()));
                BufferedOutputStream dataOut =
                        new BufferedOutputStream(new DataOutputStream(clientSocket.getOutputStream()));
                ObjectInputStream ois =
                        new ObjectInputStream(new DataInputStream(clientSocket.getInputStream()));
                ObjectOutputStream oos =
                        new ObjectOutputStream(new DataOutputStream(clientSocket.getOutputStream()))
        ) {
            boolean done = false;
//&& in.hasNextLine()
            while(!done) {

                Message message = (Message) ois.readObject();

                switch (message.type){
                    case "text":
                        if ("exit".equals(message.text)){
                            oos.writeObject(message);
                            System.out.println("system exit");
                            done = true;
                            break;
                        }
                        break;
                    case "list":
                        break;
                    case "bytes":
                        break;
                    case "pack":
                        break;
                    default:
                        break;
                }

                if(line.toLowerCase().equals("exit")) {
                    reply = relay2str("exit");

                } else if (line.toLowerCase().trim().equals("list")){
                    //todo list all files
                    File[] files = CacheClient.getFiles("list");
                    for (File file:
                         files) {
                        out.println(file.getName());
                    }
                } else if (line.startsWith("dl:")){
                    input = relay2bytes(line);

                    String filename = line.substring(3);
                    out.println("download " + filename);
                    //todo assemble
                    File file = new File(DIRECTORY + filename);
                    boolean succ = false;
                    try {
                        //succ = send(file, dataOut);
                    } catch (RuntimeException e) {
                        e.printStackTrace();
                    }
                    out.println("send succeeded " + succ);
                    System.out.println("send succeeded " + succ);
                } else if (line.toLowerCase().trim().equals("clear")){
                    reply = relay2str("clear");
                    System.out.println("clear");
                } else {
                    out.println("Enter a command");
                }
            }
            System.out.println("ended");
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public static String toString(byte[] bytes){
        StringBuilder sb = new StringBuilder();
        for (byte b:
                bytes) {
            sb.append(String.format("%02x",b));
        }
        return sb.toString();
    }
    public static String toHexString(List<byte[]> digested){
        StringBuilder sb = new StringBuilder();
        for (byte[] b:
                digested){
            sb.append(toString(b));
        }
        return sb.toString();
    }
    public byte[] relay2bytes(String message){
        try {
            return CacheClient.getBytes(message);
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }
    public String relay2str(String message){
        try {
            return CacheClient.getString(message);
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }

}
