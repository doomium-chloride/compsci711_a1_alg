import java.io.File;
import java.io.Serializable;
import java.util.List;

public class Packet implements Serializable {
    public String type, text;
    public List<List<byte[]>> pack;
    public List<File> files;
    public byte[] bytes;


    public static Packet list(List<File> files){
        Packet message = new Packet();
        message.type = "list";
        message.files = files;
        return message;
    }
    public static Packet pack(List<List<byte[]>> pack){
        Packet message = new Packet();
        message.type = "pack";
        message.pack = pack;
        return message;
    }

    public static Packet text(String text){
        Packet message = new Packet();
        message.type = "text";
        message.text = text;
        return message;
    }

    public static Packet file(String text){
        Packet message = new Packet();
        message.type = "file";
        message.text = text;
        return message;
    }
}
