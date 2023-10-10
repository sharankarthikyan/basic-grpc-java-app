import com.sharan.grpc.User.APIResponse;
import com.sharan.grpc.User.GreetEveryoneRequest;
import com.sharan.grpc.User.GreetEveryoneResponse;
import com.sharan.grpc.User.GreetManyTimesRequest;
import com.sharan.grpc.User.GreetManyTimesResponse;
import com.sharan.grpc.User.Greeting;
import com.sharan.grpc.User.LoginRequest;
import com.sharan.grpc.User.LongGreetRequest;
import com.sharan.grpc.User.LongGreetResponse;

import java.util.Iterator;

import com.sharan.grpc.userGrpc;
import com.sharan.grpc.userGrpc.userBlockingStub;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

public class GrpcClient {

	public static void main(String[] args) {

		ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090).usePlaintext().build();

		// Unary API
		System.out.println("Unary API");
		userBlockingStub userStub = userGrpc.newBlockingStub(channel);
		LoginRequest loginrequest = LoginRequest.newBuilder().setUsername("Sharan").setPassword("Sharan").build();
		APIResponse response = userStub.login(loginrequest);
		System.out.println(response.getResponsemessage());

		// Server Streaming API
		System.out.println("Server Streaming API");
		GreetManyTimesRequest greetManyTimesReq = GreetManyTimesRequest.newBuilder()
				.setGreeting(Greeting.newBuilder().setFirstName("Sharan").setLastName("Karthikeyan")).build();
		Iterator<GreetManyTimesResponse> greetManyTimesResponseItr = userStub.greetManyTimesServerStream(greetManyTimesReq);
		while (greetManyTimesResponseItr.hasNext()) {
			System.out.println(greetManyTimesResponseItr.next());
		}


		// Client Streaming API
		System.out.println("Client Streaming API");
		userGrpc.userStub asyncStub = userGrpc.newStub(channel);
		StreamObserver<LongGreetRequest> requestObserver = asyncStub.longGreet(new StreamObserver<LongGreetResponse>() {
			@Override
			public void onNext(LongGreetResponse response) {
				System.out.println("asdsadsdas");
				System.out.println("Received response: " + response.getResult());
			}

			@Override
			public void onError(Throwable t) {
				// Handle errors if necessary
			}

			@Override
			public void onCompleted() {
				channel.shutdown();
			}
		});

		// Send a stream of data to the server
		for (int i = 0; i < 5; i++) {
			LongGreetRequest request = LongGreetRequest.newBuilder()
					.setGreeting(Greeting.newBuilder().setFirstName("Sharan").setLastName("K")).build();
			requestObserver.onNext(request);
		}

		// BIDI API
		System.out.println("BIDI API");
		StreamObserver<GreetEveryoneRequest> requestObserver2 = asyncStub
				.greetEveryone(new StreamObserver<GreetEveryoneResponse>() {
					@Override
					public void onNext(GreetEveryoneResponse response) {
						System.out.println("Received from server: " + response.getResult());
					}

					@Override
					public void onError(Throwable t) {
						// Handle errors if necessary
					}

					@Override
					public void onCompleted() {
						// Handle the completion of the bidirectional exchange
						channel.shutdown();
					}
				});

		// Send and receive bidirectional messages
		for (int i = 0; i < 5; i++) {
			GreetEveryoneRequest request = GreetEveryoneRequest.newBuilder().setGreeting(Greeting.newBuilder().setFirstName("S").setLastName("K")).build();
			requestObserver2.onNext(request);
		}

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		// Signal the end of the stream
		requestObserver.onCompleted();
		requestObserver2.onCompleted();
	}
}
