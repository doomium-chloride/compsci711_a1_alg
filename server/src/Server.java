import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    final static String DIRECTORY = "samples/";
    final static int portNumber = 8011;
    Cache cache;
    Set<String> sent;
    static boolean done = false;
    public static void main(String[] args) {
        Server server = new Server();
        while(!done) {
            server.connect();
            done = true;
        }
    }

    private void init(){
        sent = new HashSet<>();
        cache = new Cache();
    }

    public void connect(){
        init();
        try (
                ServerSocket serverSocket = new ServerSocket(portNumber);
                Socket clientSocket = serverSocket.accept();
                /*PrintWriter out =
                        new PrintWriter(clientSocket.getOutputStream(), true);
                Scanner in = new Scanner(
                        new InputStreamReader(clientSocket.getInputStream()));*/
                BufferedOutputStream dataOut =
                        new BufferedOutputStream(new DataOutputStream(clientSocket.getOutputStream()));
                ObjectInputStream ois =
                        new ObjectInputStream(new DataInputStream(clientSocket.getInputStream()));
                ObjectOutputStream oos =
                        new ObjectOutputStream(new DataOutputStream(clientSocket.getOutputStream()))
        ) {
//&& in.hasNextLine()
            while(!done) {

                Message message = (Message) ois.readObject();

                switch (message.type){
                    case "text":
                        switch (message.text){
                            case "exit":
                                System.out.println("system exit");
                                done = true;
                                return;
                            case "list":

                        }
                        if ("exit".equals(message.text)){

                        } else ()
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

                String line = (String) ois.readObject();

                if(line.toLowerCase().equals("exit")) {
                    System.out.println("system exit");
                    done = true;
                } else if (line.toLowerCase().trim().equals("list")){
                    //todo list all files
                    oos.writeObject(list(dataOut));
                } else if (line.startsWith("dl:")){
                    String filename = line.substring(3);
                    //todo download selected file
                    File file = new File(DIRECTORY + filename);
                    boolean succ = false;
                    try {
                        succ = send(file, dataOut);
                    } catch (RuntimeException e) {
                        e.printStackTrace();
                    }
                    System.out.println("send succeeded " + succ);
                } else if (line.toLowerCase().trim().equals("clear")){
                    clear();
                } else {
                    System.out.println("Nothing");
                }
            }
            System.out.println("ended");
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    /*private String list(PrintWriter out){
        File folder = new File("samples");
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                out.println("File " + listOfFiles[i].getName());
            } else if (listOfFiles[i].isDirectory()) {
                out.println("Directory " + listOfFiles[i].getName());
            }
        }
        return null;
    }*/
    private List<File> list(BufferedOutputStream out){
        File folder = new File("samples");
        File[] listOfFiles = folder.listFiles();
        return Arrays.asList(listOfFiles);
    }
    private boolean send(File file, BufferedOutputStream out){//returns success
        if (!file.exists()){
            return false;
        }
        List<byte[]> fragments, hashList;
        List<List<byte[]>> pack;
        try(
                ObjectOutputStream oos = new ObjectOutputStream(out)
                ){
            fragments = cache.fragment(file);
            hashList = cache.getHashList(fragments);
            pack = pack(hashList);
            oos.writeObject(pack);
            out.flush();
        } catch (IOException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
    private void clear(){
        cache.clear();
        sent.clear();
    }
    public List<List<byte[]>> pack(List<byte[]> digested){
        List<byte[]> raw = new ArrayList<>();
        List<byte[]> hashList = new ArrayList<>();
        for (byte[] b:
                digested) {
            String hash = toString(b);
            hashList.add(b);
            if (sent.contains(hash)) {
                raw.add(null);
            } else {
                sent.add(hash);
                raw.add(cache.get(toString(b)));
            }
        }
        List<List<byte[]>> list = new ArrayList<>();
        list.add(hashList);
        list.add(raw);
        return list;
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
