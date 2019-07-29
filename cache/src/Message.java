import java.io.File;
import java.io.Serializable;
import java.util.List;

public class Message implements Serializable {
    public String type, text;
    public List<List<byte[]>> pack;
    public List<File> files;
    public byte[] bytes;
    public static Message text(String text){
        Message message = new Message();
        message.type = "text";
        message.text = text;
        return message;
    }
}
