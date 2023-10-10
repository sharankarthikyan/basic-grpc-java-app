package user;

import com.sharan.grpc.User.APIResponse;
import com.sharan.grpc.User.Empty;
import com.sharan.grpc.User.GreetEveryoneRequest;
import com.sharan.grpc.User.GreetEveryoneResponse;
import com.sharan.grpc.User.LoginRequest;
import com.sharan.grpc.User.LongGreetRequest;
import com.sharan.grpc.User.LongGreetResponse;
import com.sharan.grpc.User.GreetManyTimesRequest;
import com.sharan.grpc.User.GreetManyTimesResponse;
import com.sharan.grpc.User.Greeting;
import com.sharan.grpc.userGrpc.userImplBase;

import io.grpc.stub.StreamObserver;

public class UserService extends userImplBase{

	@Override
	public void login(LoginRequest request, StreamObserver<APIResponse> responseObserver) {
		
		System.out.println("Inside login");
		
		String username = request.getUsername();
		String password = request.getPassword();
		
		APIResponse.Builder response = APIResponse.newBuilder();
		if(username.equals(password)) {
			
			 // return success message
			
			response.setResponseCode(0).setResponsemessage("SUCCESS");
			
		}
		else {
			response.setResponseCode(100).setResponsemessage("INVALID PASSWORD");
		}
		
		
		
		responseObserver.onNext(response.build());
		responseObserver.onCompleted();
	}

	@Override
	public void logout(Empty request, StreamObserver<APIResponse> responseObserver) {
		
	}

	@Override
	public void greetManyTimesServerStream(GreetManyTimesRequest request, StreamObserver<GreetManyTimesResponse> responseObserver) {
				Greeting name = request.getGreeting();

        try {
            for (int i = 0; i < 5; i++) {
                String message = "Hello, " + name.getFirstName() +" " + name.getLastName()+ " #" + i;
                GreetManyTimesResponse response = GreetManyTimesResponse.newBuilder().setResult(message).build();
                responseObserver.onNext(response);
                Thread.sleep(1000); // Simulate some work between messages
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            responseObserver.onCompleted();
        }
	}	
	
	@Override
	public StreamObserver<LongGreetRequest> longGreet(StreamObserver<LongGreetResponse> responseObserver) {
			return new StreamObserver<LongGreetRequest>() {
					private StringBuilder dataBuilder = new StringBuilder();

					@Override
					public void onNext(LongGreetRequest request) {
							Greeting greeting = request.getGreeting();
							dataBuilder.append(greeting.getFirstName() + " " + greeting.getLastName());
							System.out.println("->Received: " + greeting);
					}

					@Override
					public void onError(Throwable t) {
							// Handle errors if necessary
					}

					@Override
					public void onCompleted() {
							String combinedData = dataBuilder.toString();
							LongGreetResponse response = LongGreetResponse.newBuilder().setResult("Combined data: " + combinedData).build();
							responseObserver.onNext(response);

							try {
								Thread.sleep(1000);
							} catch(Exception e) {

							}

							responseObserver.onCompleted();
					}
			};
	}
	
	@Override
    public StreamObserver<GreetEveryoneRequest> greetEveryone(StreamObserver<GreetEveryoneResponse> responseObserver) {
        return new StreamObserver<GreetEveryoneRequest>() {
            @Override
            public void onNext(GreetEveryoneRequest request) {
                Greeting data = request.getGreeting();
                System.out.println("Received from client: " + data);

                String responseMessage = "Server response to: " + data;
                GreetEveryoneResponse response = GreetEveryoneResponse.newBuilder().setResult(responseMessage).build();
                responseObserver.onNext(response);
            }

            @Override
            public void onError(Throwable t) {
                // Handle errors if necessary
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
    }
	 
}
