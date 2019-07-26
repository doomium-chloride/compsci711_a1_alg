import java.io.*;
import java.net.*;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class Server {
    final static int portNumber = 8011;
    Cache cache;
    Set<String> sent;
    public static void main(String[] args) {
        new Server().connect();
    }

    public void connect(){
        sent = new HashSet<>();
        cache = new Cache();
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
    private boolean send(File file){//returns success
        List<byte[]> fragments;
        try{
            fragments = cache.fragment(file);
        } catch (IOException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
