
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Relay {
    final static String DIRECTORY = "samples/";
    final static int portNumber = 8010;//connects to client
    static CacheClient cacheClient;
    public static void main(String[] args) {
        new Relay().connect();
    }

    private void init(){
        cacheClient = new CacheClient();
    }

    public void connect(){
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
            while(!done){

                Packet message = (Packet) ois.readObject();

                switch (message.type){
                    case "text":
                        switch (message.text){
                            case "exit":
                                cacheClient.send(message);
                                System.out.println("system exit");
                                done = true;
                                break;
                            case "clear":
                                cacheClient.send(message);
                                Builder.clear();
                                break;
                            case "list":
                                cacheClient.send(message);
                                Packet listReply = CacheClient.read(ois);
                                break;
                        }
                    case "file":
                        cacheClient.send(message);
                        break;
                    case "list":
                        List<File> fileList = message.files;
                        String listStr = list2str(fileList);
                        Packet sendList = Packet.text(listStr);
                        cacheClient.send(sendList);
                        break;
                    case "bytes":
                        break;
                    case "pack":
                        byte[] bytes = Builder.assemble(message.pack);
                        Packet sendBytes = Packet.bytes(bytes);
                        cacheClient.send(sendBytes);
                        break;
                    default:
                        System.out.println("Invalid command");
                        break;
                }
            }
            System.out.println("ended");
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    private String list2str(List<File> files){
        StringBuilder stringBuilder = new StringBuilder();
        for (File file:
             files) {
            stringBuilder.append(file.getName());
            stringBuilder.append('\n');
        }
        return stringBuilder.toString();
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

}
