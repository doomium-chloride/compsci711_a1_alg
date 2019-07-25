package server;

import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.Scanner;

public class Server {
    final static int portNumber = 8011;

    public static void main(String[] args) {
        connect();
    }

    public static void connect(){
        try (
                ServerSocket serverSocket = new ServerSocket(portNumber);
                Socket clientSocket = serverSocket.accept();
                PrintWriter out =
                        new PrintWriter(clientSocket.getOutputStream(), true);
                Scanner in = new Scanner(
                        new InputStreamReader(clientSocket.getInputStream()));
        ) {
            boolean done = false;

            while(!done && in.hasNextLine()) {
                String line = in.nextLine();
                out.println("Echo from <Your Name Here> Server: " + line);

                if(line.toLowerCase().trim().equals("peace")) {
                    done = true;
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
