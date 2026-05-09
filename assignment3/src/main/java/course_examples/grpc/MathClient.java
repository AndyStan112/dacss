package course_examples.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class MathClient {

    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        MathServiceGrpc.MathServiceBlockingStub stub =
                MathServiceGrpc.newBlockingStub(channel);

        BinaryOpRequest request = BinaryOpRequest.newBuilder()
                .setA(5)
                .setB(3)
                .build();

        BinaryOpResponse addResponse = stub.add(request);
        System.out.println("Add: " + addResponse.getResult());

        BinaryOpResponse multResponse = stub.mult(request);
        System.out.println("Mult: " + multResponse.getResult());

        channel.shutdown();
    }
}