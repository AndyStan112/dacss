package grpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;

public class BankServerMain {

    public static void main(String[] args) {
        try {
            Server server = ServerBuilder
                    .forPort(50052)
                    .addService(new BankImpl())
                    .build();

            server.start();
            System.out.println("Bank server started on port 50052");

            server.awaitTermination();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}