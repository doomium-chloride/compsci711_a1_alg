import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
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
    public boolean rabin(byte[] bytes, int start, int max){
        byte[] copy = Arrays.copyOfRange(bytes, start, Math.min(start + 3, max));
        byte[] hash = messageDigest.digest(copy);
        int x = new BigInteger(hash).intValue()%byteLen;
        return x == 0;
    }
    @SuppressWarnings("Duplicates")
    public List<byte[]> fragment(byte[] bytes) {
        List<byte[]> list = new ArrayList<>();
        int len = bytes.length;
        int start = 0;
        int left = start;
        int end = 3;
        int right = end;
        int longest = 0;
        while (true) {
            if (end >= len){
                right = len;
                byte[] copy = Arrays.copyOfRange(bytes, left, right);
                list.add(copy);
                break;
            }
            if (rabin(bytes, start, end)){
                right = end;
                byte[] copy = Arrays.copyOfRange(bytes, left, right);
                list.add(copy);
                left = right;
                longest = Math.max(longest, copy.length);
            }
            end++;start++;
        }
        System.out.println("longest cache size: " + longest);
        System.out.println("Average cache size: " + bytes.length/list.size());
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
    public byte[] get(String key){
        return map.get(key);
    }

}
