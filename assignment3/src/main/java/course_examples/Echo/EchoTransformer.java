package course_examples.Echo;
import RequestReply.*;

public class EchoTransformer implements ByteStreamTransformer {

    @Override
    public byte[] transform(byte[] input) {
       // System.out.println("EchoTransformer received input of length "+input.length);

        byte[] result = new byte[input.length+5];
        result[0] = 'E'; result[1] = 'c'; result[2] = 'h'; result[3] = 'o'; result[4] = ':';
        for (int i = 0; i < input.length; i++) {
            result[i+5] = input[i];
            }
       // System.out.println("EchoTransformer produced result of length "+result.length);

        return result;
    }
}