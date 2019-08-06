import java.io.*;
import java.net.Socket;
import java.util.List;

public class Client {
    final static String host = "localhost";
    final static int portNumber = 8010;
    Socket socket;
    ObjectInputStream ois;
    ObjectOutputStream oos;
    GUIpanel guiPanel;
    public void connect(){
        init();
        while(true){
            String input = Keyboard.readInput();
            try {
                command(input);
            } catch (Exception e){
                throw new RuntimeException(e);
            }
            if ("exit".equalsIgnoreCase(input)){
                break;
            }
        }
    }

    public void init() {
    try {
        System.out.println("Creating socket to '" + host + "' on port " + portNumber);
        socket = new Socket(host, portNumber);
        oos = new ObjectOutputStream(new DataOutputStream(socket.getOutputStream()));
        ois = new ObjectInputStream(new DataInputStream(socket.getInputStream()));
    } catch (IOException e){
        throw new RuntimeException(e);
    }

    }

    public void command(String input) throws IOException, ClassNotFoundException{
        input = input.trim();
        if (input.startsWith("dl:")){
            String fileName = input.substring(3);
            Packet fileReq = Packet.file(fileName);
            oos.writeObject(fileReq);//send request
            Packet fileGet = getReply();//get file
            if (fileGet.type.equals("text")){//fail case
                System.out.println("file transfer went wrong");
                System.out.println(fileGet.text);
            } else {//success
                byte[] fileBytes = fileGet.bytes;
                File picFile = makeFile(fileBytes, fileName);
                System.out.println("downloaded filename");
                if (picFile != null && picFile.exists()){
                    guiPanel.showPic(picFile);
                }
            }
        } else {
            input = input.toLowerCase();
            switch (input){
                case "clear":
                case "exit":
                    sendCommand(input);
                    break;
                case "list":
                    sendCommand(input);
                    Packet reply = getReply();
                    List<File> files = reply.files;
                    for (File file:
                         files) {
                        System.out.println(file.getName());
                    }
                    break;
                default:
                    System.out.println("Invalid command");
            }
        }
    }

    public List<File> getList() throws IOException, ClassNotFoundException{
        sendCommand("list");
        Packet reply = getReply();
        return reply.files;
    }

    public void sendCommand(String command) throws IOException{
        oos.writeObject(Packet.text(command));
    }

    private Packet getReply() throws IOException, ClassNotFoundException{
        return (Packet) ois.readObject();
    }

    public static void main(String[] args) {
        new Client().connect();
    }

    public File makeFile(byte[] bytes, String name){
        File newFile = new File("testing/" + name);
        try{
            OutputStream os = new FileOutputStream(newFile);
            os.write(bytes);
            os.close();
            return newFile;
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

}
