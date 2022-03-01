package bif3.swe1.rest;

import bif3.swe1.rest.classes.RequestContext;
import javafx.util.Pair;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

import static bif3.swe1.rest.classes.RequestContext.breakUpRequest;

public class Server implements Runnable {
    private static ServerSocket _listener = null;

    static ArrayList<String> messages = new ArrayList<>();

    public static void main(String[] args) {

        try {
            _listener = new ServerSocket(8080, 5);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        Runtime.getRuntime().addShutdownHook(new Thread(new Server()));

        System.out.println("Listening for connection on port 10001 ....");
        try {
            while (true) {
                try (Socket socket = _listener.accept()) {
                    InputStream is = socket.getInputStream();
                    BufferedReader buf = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));

                    String request = "";
                    int character;

                    String line;
                    int conLen = 0;
                    do {
                        line = buf.readLine();
                        System.out.println(line);
                        if(line.toLowerCase().startsWith("content-length:"))
                            conLen = Integer.parseInt(line.split(": ")[1]);
                        request = request.concat(line+"\r\n");
                    } while (!line.isEmpty());



                    while (conLen-- > 0) {
                        character = buf.read();
                        request = request.concat(String.valueOf((char) character));
                    }
                    System.out.println(request);

                    if (request.isEmpty()) {
                        buf.close();
                        is.close();
                        continue;
                    }
                    OutputStream os = socket.getOutputStream();

                    HashMap<String, String> headerAndBody = breakUpRequest(request);
                    System.out.println(headerAndBody);
                    Pair<ArrayList<String>, String> response = RequestContext.handleRequest(headerAndBody.get("method"), headerAndBody.get("path"), headerAndBody.get("body"), headerAndBody.get("Accept"), headerAndBody.get("token"), messages);

                    messages = response.getKey();

                    os.write(response.getValue().getBytes(StandardCharsets.UTF_8));

                    os.flush();
                    os.close();
                    buf.close();
                    is.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            _listener.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        _listener = null;
        System.out.println("close server");
    }
}
