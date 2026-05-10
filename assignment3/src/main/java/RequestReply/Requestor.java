package RequestReply;

import java.net.*;
import java.io.*;


/**
 * The Requestor establishes a TCP connection to a server,
 * sends a request message, and waits for a response.
 */
public class Requestor {

    private final String name;

    public Requestor(String name) {
        this.name = name;
    }
 
   /**
     * Sends a byte array request to the specified host and port,
     * waits for a response, and returns the response as a byte array.
     */
    public byte[] sendRequestAndWaitResponse(String host, int port, byte[] data) {
        try (Socket socket = new Socket(host, port);
             OutputStream out = socket.getOutputStream();
             InputStream in = socket.getInputStream()) {

            System.out.println(name + " connected: " + socket);

            writeMessage(out, data);
            System.out.println(name + " waiting for response ");

            return readMessage(in);

        } catch (IOException e) {
            System.out.println("Requestor IO error");
            e.printStackTrace();
            return null;
        }
    }

    private void writeMessage(OutputStream out, byte[] data) throws IOException {
        DataOutputStream dos = new DataOutputStream(out);
        dos.writeInt(data.length); // Send first the length of the message 
        dos.write(data);           // Send the message data
        dos.flush();
    }

    private byte[] readMessage(InputStream in) throws IOException {
        DataInputStream dis = new DataInputStream(in);
        int length = dis.readInt(); // Read the length of the incoming message
        byte[] data = new byte[length]; // Allocate buffer for incoming message
        dis.readFully(data);            // Read message
        return data;
    }
}