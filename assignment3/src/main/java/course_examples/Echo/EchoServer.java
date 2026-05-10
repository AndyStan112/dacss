package course_examples.Echo;
import RequestReply.*;

public class EchoServer {
    public static void main(String[] args) throws Exception {
        Replier r = new Replier("Server", 9000, 10);
        ByteStreamTransformer t = new EchoTransformer();
        r.start(t); 
    }
}