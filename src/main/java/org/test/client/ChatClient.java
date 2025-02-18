package org.test.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.function.Consumer;

public class ChatClient {
    private Socket socket;
    private BufferedReader inputConsole;
    private PrintWriter out;
    private BufferedReader in;
    private Consumer<String> onMessageReceived;

    public ChatClient(String address, int port) {
        try {
            socket = new Socket(address, port);
            System.out.println("Connected to the server: " + address + ":" + port);
            inputConsole = new BufferedReader(new InputStreamReader(System.in));
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line = "";
            while(!line.equals("exit")) {
                line = inputConsole.readLine();
                out.println(line);
                System.out.println("Server response: " + in.readLine());
            }

            socket.close();
            inputConsole.close();
            out.close();
        } catch (UnknownHostException e) {
            System.out.println("Unknown host: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("I/O error: " + e.getMessage());
        }
    }

    public ChatClient(String address, int port, Consumer<String> onMessageReceived) throws IOException {
        this.socket = new Socket(address, port);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.onMessageReceived = onMessageReceived;
    }

    public void sendMessage(String msg) {
        out.println(msg);
    }

    public void startClient() {
        new Thread(() -> {
            try {
                String msg;
                while ((msg = in.readLine()) != null) {
                    onMessageReceived.accept(msg);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void main(String[] args) {
        ChatClient client = new ChatClient("127.0.0.1", 5000);
    }
}
