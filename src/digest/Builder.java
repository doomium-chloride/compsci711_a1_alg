package digest;

import java.util.*;

public class Builder {
    public static Map<String, byte[]> map = new HashMap<>();
    public static Set<String> sent = new HashSet<>();
    public static List<List<byte[]>> construct(byte[] file){
        List<byte[]> chunks = Digest.digest(file);
        List<byte[]> raw = new ArrayList<>();
        List<byte[]> digested = Digest.getHashList(file);
        List<byte[]> hashList = new ArrayList<>();
        for (byte[] b:
             digested) {
            String hash = Digest.toString(b);
            hashList.add(b);
            if (sent.contains(hash)) {
                raw.add(null);
            } else {
                sent.add(hash);
                raw.add(map.get(Digest.toString(b)));
            }
        }
        List<List<byte[]>> list = new ArrayList<>();
        list.add(hashList);
        list.add(raw);
        return list;
    }
    public static byte[] assemble(List<List<byte[]>> pairs){
        List<byte[]> list = pairs.get(0);
        List<byte[]> raw = pairs.get(1);
        List<Byte> byteList = new ArrayList<>();
        int length = list.size();
        int cached = 0;
        for (int i = 0; i < length; i++) {
            byte[] rawData = raw.get(i);
            byte[] hash = list.get(i);
            if (rawData == null){
                rawData = map.get(Digest.toString(hash));
                cached++;
            }
            for (byte piece:
                    rawData) {
                byteList.add(piece);
            }
        }
        System.out.println(100.0*cached/length + "% from cached");
        int len = byteList.size();
        byte[] bytes = new byte[len];
        for (int i = 0; i < len; i++) {
            bytes[i] = byteList.get(i);
        }
        return bytes;
    }
    public static void clear(){
        map.clear();
        sent.clear();
    }
}
