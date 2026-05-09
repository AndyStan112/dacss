package course_examples.grpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;

public class MathServerMain {

    public static void main(String[] args) {
        try {
            Server server = ServerBuilder
                    .forPort(50051)
                    .addService(new MathImpl()) // service implemengtation
                    .build();

            server.start();
            System.out.println("Server started on port 50051");

            server.awaitTermination();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}