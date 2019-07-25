package client;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Cacher {
    final static String host = "localhost";
    final static int portNumber = 8011;
    public static void main(String[] args) {
        try {
            connect("test1.bmp");
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    public static void connect(String filename) throws IOException {

        System.out.println("Creating socket to '" + host + "' on port " + portNumber);

        while (true) {
            Socket socket = new Socket(host, portNumber);
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            //byte[] input1 = read(dataInputStream);

            Scanner userInputScanner = new Scanner(new InputStreamReader(System.in));
            String userInput = userInputScanner.nextLine();

            out.println(userInput);

            //byte[] input2 = read(dataInputStream);

            if ("exit".equalsIgnoreCase(userInput)) {
                socket.close();
                break;
            }
        }
    }

    private static byte[] read(DataInputStream input) throws IOException{
        int length = input.readInt();                    // read length of incoming message
        if(length>0) {
            byte[] message = new byte[length];
            input.readFully(message, 0, message.length); // read the message
            return message;
        } else {
            return new byte[]{};
        }
    }
}
