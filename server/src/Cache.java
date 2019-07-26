import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class Cache {
    static final int byteLen = 2000;
    private Map<String,byte[]> map;
    private MessageDigest messageDigest;
    public Cache(){
        map = new HashMap<>();
        try {
            messageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e){
            throw new RuntimeException(e);
        }
    }
    public byte[] getBytes(File file) throws IOException {
        return Files.readAllBytes(file.toPath());
    }
    public byte[] getBytes(String filePath) throws IOException {
        File file = new File(filePath);
        return getBytes(file);
    }
    public List<byte[]> fragment(File file) throws IOException{
        return fragment(getBytes(file));
    }
    @SuppressWarnings("Duplicates")
    public List<byte[]> fragment(byte[] bytes){
        List<byte[]> list = new ArrayList<>();
        for (int i = 0; i < bytes.length; i += byteLen) {
            byte[] array = Arrays.copyOfRange(bytes, i, Math.min(i + byteLen, bytes.length));
            list.add(array);
        }
        return list;
    }
    public List<byte[]> getHashList(List<byte[]> digest){
        List<byte[]> digested = new ArrayList<>();
        for (byte[] d:
                digest) {
            byte[] fingerPrint = messageDigest.digest(d);
            digested.add(fingerPrint);
            String key = toString(fingerPrint);
            if (!map.containsKey(key)) {
                map.put(key, d);
            }
        }
        return digested;
    }
    public String toString(byte[] bytes){
        StringBuilder sb = new StringBuilder();
        for (byte b:
                bytes) {
            sb.append(String.format("%02x",b));
        }
        return sb.toString();
    }
    public void clear(){
        map.clear();
    }
}
