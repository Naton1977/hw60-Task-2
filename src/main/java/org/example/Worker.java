package org.example;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;

public class Worker implements Runnable {
    Socket remoteClient;
    String clientMessage;
    String requestType;
    String host;
    String userAgent;
    String accept;
    String connection;
    String resource;
    int indexRequest;
    int indexSlash;
    int indexHost;
    int indexAccept;
    int indexUserAgent;
    int indexConnection;
    int indexProtocol;

    public Worker(Socket remoteClient) {
        this.remoteClient = remoteClient;
    }

    @Override
    public void run() {
        try (OutputStream outputStream = remoteClient.getOutputStream();
             BufferedReader inputStream = new BufferedReader(new InputStreamReader(remoteClient.getInputStream()))) {
            while (true) {
                clientMessage = inputStream.readLine();
                indexRequest = clientMessage.indexOf("GET");
                if (indexRequest > -1) {
                    requestType = clientMessage;
                }
                indexHost = clientMessage.indexOf("Host:");
                if (indexHost > -1) {
                    host = clientMessage;
                }
                indexUserAgent = clientMessage.indexOf("User-Agent:");
                if (indexUserAgent > -1) {
                    userAgent = clientMessage;
                }
                indexAccept = clientMessage.indexOf("Accept:");
                if (indexAccept > -1) {
                    accept = clientMessage;
                }
                indexConnection = clientMessage.indexOf("Connection:");
                if (indexConnection > -1) {
                    connection = clientMessage;
                }
                if (clientMessage.equals("")) {
                    break;
                }

            }
            if (requestType.length() > 0) {
                indexSlash = requestType.indexOf("/");
                indexProtocol = requestType.indexOf("HTTP");
                resource = requestType.substring(indexSlash, indexProtocol);
                if (resource.length() == 2) {
                    String fileName = "index.html";
                    findFile(fileName, outputStream);
                } else {
                    resource = requestType.substring((indexSlash + 1), indexProtocol);
                    String tmp = resource.trim();
                    findFile(tmp, outputStream);
                    System.out.println(tmp);
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void findFile(String fileName, OutputStream outputStream) throws IOException {
        String outMessage = "";
        String message;
        String responce = "";
        File file = new File("pages/" + fileName);
        if (file.exists()) {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            while ((message = bufferedReader.readLine()) != null){
                String temp = message.trim();
                outMessage += temp;
            }

            responce = "HTTP/1.1 200 OK\r\n" +
                    LocalDateTime.now().toString() + "\r\n" +
                    "Server: Apache/2.4.41 (Win64) OpenSSL/1.1.1c PHP/7.4.4\r\n" +
                    "X-Powered-By: PHP/7.4.4\r\n" +
                    "Content-Length: " + outMessage.length() + "\r\n" +
                    "Connection: close\r\n" +
                    "Content-Type: text/html; charset=UTF-8\r\n" +
                    "\r\n" +
                    outMessage + "\r\n";
            outputStream.write(responce.getBytes());


        } else {
           fileNotFound("404.html", outputStream);
        }
        System.out.println(responce);
    }
    public void fileNotFound(String fileName, OutputStream outputStream) throws IOException {
        String outMessage = "";
        String message;
        String responce = null;
        File file = new File("pages/" + fileName);
        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        while ((message = bufferedReader.readLine()) != null){
            String temp = message.trim();
            outMessage += temp;
        }

        responce = "HTTP/1.1 404 Not Found\r\n" +
                LocalDateTime.now().toString() + "\r\n" +
                "Server: Apache/2.4.41 (Win64) OpenSSL/1.1.1c PHP/7.4.4\r\n" +
                "X-Powered-By: PHP/7.4.4\r\n" +
                "Content-Length: " + outMessage.length() + "\r\n" +
                "Connection: close\r\n" +
                "Content-Type: text/html; charset=UTF-8\r\n" +
                "\r\n" +
                outMessage + "\r\n";
        outputStream.write(responce.getBytes());
        System.out.println(responce);
    }

}
