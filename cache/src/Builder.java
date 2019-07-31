import java.util.*;

public class Builder {
    public static Map<String, byte[]> map = new HashMap<>();

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
                rawData = map.get(Relay.toString(hash));
                cached++;
            }
            else{
                map.put(Relay.toString(hash),rawData);
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
    }
}
