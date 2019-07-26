import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    final static String DIRECTORY = "samples/";
    final static int portNumber = 8011;
    Cache cache;
    Set<String> sent;
    public static void main(String[] args) {
        new Server().connect();
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
                PrintWriter out =
                        new PrintWriter(clientSocket.getOutputStream(), true);
                Scanner in = new Scanner(
                        new InputStreamReader(clientSocket.getInputStream()));
                BufferedOutputStream dataOut =
                        new BufferedOutputStream(new DataOutputStream(clientSocket.getOutputStream()));
        ) {
            boolean done = false;

            while(!done && in.hasNextLine()) {
                String line = in.nextLine();
                out.println("Command: " + line);

                if(line.toLowerCase().trim().equals("exit")) {
                    done = true;
                } else if (line.toLowerCase().trim().equals("list")){
                    //todo list all files
                    out.println("list files");
                    list(out);
                } else if (line.startsWith("dl:")){
                    String filename = line.substring(3);
                    out.println("download " + filename);
                    //todo download selected file
                    File file = new File(DIRECTORY + filename);
                    boolean succ = send(file, dataOut);
                    System.out.println("send succeeded " + succ);
                } else if (line.toLowerCase().trim().equals("clear")){
                    clear();
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    private String list(PrintWriter out){
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
    }
    private boolean send(File file, BufferedOutputStream out){//returns success
        List<byte[]> fragments, hashList;
        List<List<byte[]>> pack;
        try(
                ObjectOutputStream oos = new ObjectOutputStream(out)
                ){
            fragments = cache.fragment(file);
            hashList = cache.getHashList(fragments);
            pack = pack(hashList);
            oos.writeObject(pack);
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
