package digest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class Digest {
    static MessageDigest messageDigest;
    public static final int byteLen = 2000;
    public static byte[] getBytes(File file) throws IOException {
        return Files.readAllBytes(file.toPath());
    }
    public static List<byte[]> getHashList(byte[] bytes){ ;

        List<byte[]> digest = digest(bytes);
        List<byte[]> digested = new ArrayList<>();
        for (byte[] d:
                digest) {
            byte[] fingerPrint = messageDigest.digest(d);
            digested.add(fingerPrint);
            String key = toString(fingerPrint);
            if (!Builder.map.containsKey(key)) {
                Builder.map.put(key, d);
            }
        }
        return digested;
    }
    public static String getHash(byte[] bytes){
        List<byte[]> digested = getHashList(bytes);
        return toHexString(digested);
    }
    /*public static List<byte[]> digest(byte[] bytes){
        List<byte[]> list = new ArrayList<>();
        for (int i = 0; i < bytes.length; i += byteLen) {
            byte[] array = Arrays.copyOfRange(bytes, i, Math.min(i + byteLen, bytes.length));
            list.add(array);
        }
        return list;
    }*/
    public static boolean rabin(byte[] bytes, int start, int max){
        byte[] copy = Arrays.copyOfRange(bytes, start, Math.min(start + 3, max));
        byte[] hash = messageDigest.digest(copy);
        int x = new BigInteger(hash).intValue()%2000;
        if (x != 0){
            return false;
        }
        return true;
    }
    public static List<byte[]> digest(byte[] bytes){
        List<byte[]> list = new ArrayList<>();
        int len = bytes.length;
        int start = 0;
        int left = start;
        int end = 3;
        int right = end;
        int breaks = 0;
        while(true){
            if (end >= len){
                right = len;
                byte[] copy = Arrays.copyOfRange(bytes, left, right);
                list.add(copy);
                breaks++;
                break;
            }
            if (rabin(bytes, start, end)){
                right = end;
                byte[] copy = Arrays.copyOfRange(bytes, left, right);
                list.add(copy);
                breaks++;
                left = right;
            }
            end++;start++;
        }
        System.out.println(bytes.length/breaks + "KB");
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


    private static String test(String filename){
        File file = new File("samples/" + filename);
        try {
            byte[] bytes = getBytes(file);
            String x = getHash(bytes);
            return x;
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static void makeFile(byte[] bytes, String name){
        File newFile = new File("samples/" + name);
        try{
            OutputStream os = new FileOutputStream(newFile);
            os.write(bytes);
            os.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void testBuild(String read, String write){
        File file = new File("samples/" + read);
        try {
            byte[] bytes = getBytes(file);
            List<List<byte[]>> pairs = Builder.construct(bytes);
            byte[] built = Builder.assemble(pairs);
            makeFile(built, write);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void init(){
        try {
            messageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e){
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        init();
        /*test("test1.bmp");
        String a = test("test1.bmp");
        String b = test("test2.bmp");
        test("test3.bmp");
        test("test1.txt");
        test("test2.txt");
        File file = new File("samples/test1.bmp");
        try {
            byte[] bytes = getBytes(file);
            makeFile(bytes);
            System.out.println("done");
        } catch (Exception e){
            e.printStackTrace();
        }
        System.out.println(a.compareTo(b));
        int x = a.length();
        for (int i = 0; i < x; i++) {
            if (a.charAt(i) != b.charAt(i)){
                System.out.println(a.charAt(i)+"-"+b.charAt(i));
            }
        }*/
        testBuild("test1.bmp","build1.bmp");
        testBuild("test1.bmp","build1.bmp");
        testBuild("test2.bmp","build2.bmp");
        Builder.clear();
        testBuild("test2.bmp","build2.bmp");
    }
}
