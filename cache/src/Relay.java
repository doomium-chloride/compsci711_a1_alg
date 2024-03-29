
import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Relay {
    final static String DIRECTORY = "samples/";
    final static int portNumber = 8010;//connects to client
    static CacheClient cacheClient;
    static boolean done = false;
    ServerSocket serverSocket;
    Socket clientSocket;
    ObjectOutputStream oos;
    ObjectInputStream ois;
    Logger logger;
    CacheGUI cacheGUI;
    public static void main(String[] args) {
        Relay cache = new Relay();
        while(!done) {
            try {
                cache.connect();
                done = true;
            } catch (RuntimeException e){
                e.printStackTrace();
            }
        }
    }

    public Relay(){
        super();
        logger = new Logger();
    }

    private void init(){
        try {
            cacheClient = new CacheClient();
            System.out.println("Cache server initialised, port: " + portNumber);
            serverSocket = new ServerSocket(portNumber);
            clientSocket = serverSocket.accept();
            oos = new ObjectOutputStream(new DataOutputStream(clientSocket.getOutputStream()));
            ois = new ObjectInputStream(new DataInputStream(clientSocket.getInputStream()));
            System.out.println("streams created, port: " + portNumber);
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    public void connect(){
        init();
        while(!done){
            try {

    //&& in.hasNextLine()

                if (cacheGUI != null){
                    cacheGUI.updateLog();
                }
                System.out.println(logger.toString());
                System.out.println("waiting for new command");

                Packet message = (Packet) ois.readObject();//Message received from client
                if (message == null){
                    System.out.println("Problem! message is null");
                }
                //clientCache.send means send to server
                //oos.writeObject means sent to client
                System.out.println("type=" + message.type);
                switch (message.type){
                    case "text":
                        System.out.println("text=" + message.text);
                        switch (message.text){
                            case "exit":
                                cacheClient.send(message);
                                System.out.println("system exit");
                                done = true;
                                logger.record("exit");
                                cacheGUI.terminate();
                                break;
                            case "clear":
                                cacheClient.send(message);
                                Builder.clear();
                                logger.record("clear cache");
                                break;
                            case "list":
                                cacheClient.send(message);
                                Packet listReply = cacheClient.read();
                                oos.writeObject(listReply);
                                logger.record("request list",
                                        list2str(listReply.files));
                                break;
                        }
                        break;
                    case "file":
                        cacheClient.send(message);
                        Packet pack = cacheClient.read();
                        if (pack.type.equals("text")){
                            oos.writeObject(pack);//relay fail message
                            logger.record("file request", "failed");
                        } else {
                            byte[] bytes = Builder.assemble(pack.pack);
                            Packet sendBytes = Packet.bytes(bytes);
                            String fileGetName = pack.text;
                            oos.writeObject(sendBytes);
                            logger.record("file request", "success");
                            logger.record("filename is", fileGetName);
                            logger.record("file from cache", Builder.message);
                        }
                        break;
                    case "list":
                        System.out.println("Shouldn't receive bytes from client");
                        break;
                    case "bytes":
                        System.out.println("Shouldn't receive bytes from client");
                        break;
                    case "pack":
                        System.out.println("Shouldn't receive pack from client");
                        break;
                    default:
                        System.out.println("Invalid command");
                        break;

                }
                oos.flush();

            }
            catch (Exception e){
                e.printStackTrace();
                reset();
            }
            System.out.println("ended");
        }
        System.out.println(logger.toString());
    }
    private void reset(){
        try{
            ois.close();
            oos.close();
            serverSocket.close();
            init();
        } catch (IOException e){
            throw new RuntimeException(e);
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
