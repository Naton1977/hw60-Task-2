package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;


public class Client {
    public static void main(String[] args) throws IOException {
        String serverMessage;
        Socket client = new Socket();
        client.connect(new InetSocketAddress(InetAddress.getLocalHost(), 10000));
        try (OutputStream out = client.getOutputStream();
             BufferedReader inputStream = new BufferedReader(new InputStreamReader(client.getInputStream()))) {
            String request = "GET / HTTP/1.1\r\n" +
                    "Host: localhost\r\n" +
                    "User-Agent: Console-Browser. It is not Mozilla\r\n" +
                    "Accept: text/html\r\n" +
                    "Connection: close\r\n" +
                    "\r\n";
            out.write(request.getBytes());
            out.flush();

            while ((serverMessage = inputStream.readLine()) != null){
                System.out.println(serverMessage);
            }
        }
    }


}
