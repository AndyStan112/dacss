package course_examples.Echo;
import RequestReply.*;


public class EchoClient {
    public static void main(String[] args) {
        Requestor req = new Requestor("Client");

        byte[] response =
            req.sendRequestAndWaitResponse("localhost", 9000, "hello".getBytes());

        System.out.println(new String(response));

    }
}