package RequestReply;

import java.net.*;
import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Multithreaded Replier
 * start(transformer) starts a serverloopwhere it receives messages,
 * forwards them to the transformer, and sends back responses
 */
public class Replier {

    private final ServerSocket serverSocket;
    private final String name;
    private final ExecutorService threadPool;
    private volatile boolean running = true;

    public Replier(String name, int port, int poolSize) throws IOException {
        this.name = name;
        this.serverSocket = new ServerSocket(port);
        this.threadPool = Executors.newFixedThreadPool(poolSize);
        System.out.println(name + " listening on port " + port);
    }

    /**
     * Starts the server loop
     */
    public void start(ByteStreamTransformer transformer) {
        while (running) {
            try {
                Socket socket = serverSocket.accept();
                System.out.println(name + " accepted: " + socket);

                // Delegate client handling to thread pool
                threadPool.submit(() -> handleClient(socket, transformer));

            } catch (IOException e) {
                if (running) {
                    System.out.println(name + " accept error");
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Handles a single client connection.
     */
    private void handleClient(Socket socket, ByteStreamTransformer transformer) {
        try (Socket s = socket;
             InputStream in = s.getInputStream();
             OutputStream out = s.getOutputStream()) {

            byte[] request = readMessage(in);
            System.out.println(name + " received from " + s.getRemoteSocketAddress());

            byte[] response = transformer.transform(request);

            writeMessage(out, response);

        } catch (IOException e) {
            System.out.println(name + " client error");
            e.printStackTrace();
        }
    }

    private byte[] readMessage(InputStream in) throws IOException {
        DataInputStream dis = new DataInputStream(in);
        int length = dis.readInt();

        if (length <= 0 || length > 10_000_000) {
            throw new IOException("Invalid message length: " + length);
        }

        byte[] data = new byte[length];
        dis.readFully(data);
        return data;
    }

    private void writeMessage(OutputStream out, byte[] data) throws IOException {
        DataOutputStream dos = new DataOutputStream(out);
        dos.writeInt(data.length);
        dos.write(data);
        dos.flush();
    }

    
    public void stop() {
        running = false;
        threadPool.shutdown();

        try {
            serverSocket.close();
        } catch (IOException e) {
            System.out.println(name + " error closing server");
        }
    }
}