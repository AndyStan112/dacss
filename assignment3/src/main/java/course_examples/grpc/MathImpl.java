package course_examples.grpc;

import io.grpc.stub.StreamObserver;

public class MathImpl extends MathServiceGrpc.MathServiceImplBase {

    @Override
    public void add(BinaryOpRequest request, StreamObserver<BinaryOpResponse> responseObserver) {
        
        int result = request.getA() + request.getB();

        System.out.println("MathImpl performed add, result="+result);

        BinaryOpResponse response = BinaryOpResponse.newBuilder()
                .setResult(result)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void mult(BinaryOpRequest request, StreamObserver<BinaryOpResponse> responseObserver) {

        int result = request.getA() * request.getB();

        System.out.println("MathImpl performed mult, result="+result);

        BinaryOpResponse response = BinaryOpResponse.newBuilder()
                .setResult(result)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}