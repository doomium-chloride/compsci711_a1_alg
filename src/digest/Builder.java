package digest;

import java.util.*;

public class Builder {
    public static Map<String, byte[]> map = new HashMap<>();
    public static Set<String> sent = new HashSet<>();
    public static List<byte[]> construct(byte[] file){
        List<byte[]> digested = Digest.getHashList(file);
        List<byte[]> hashList = new ArrayList<>();
        for (byte[] b:
             digested) {
            String hash = Digest.toString(b);
            if (sent.contains(hash)) {
                hashList.add(b);
            } else {
                sent.add(hash);
                hashList.add(null);
            }
        }
        return hashList;
    }
    public static byte[] assemble(List<byte[]> list){
        byte[] bytes;
        int length = 0;
        for (byte[] b:
             list) {
            length += b.length;
        }
        bytes = new byte[length];
        int i = 0;
        for (byte[] array:
                list) {
            if (array != null) {
                for (byte b :
                        array) {
                    bytes[i] = b;
                    i++;
                }
            }
        }
        return bytes;
    }
    public static void clear(){
        map.clear();
        sent.clear();
    }
}
