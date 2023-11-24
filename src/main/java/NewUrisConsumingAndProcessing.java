import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public enum NewUrisConsumingAndProcessing {
    INSTANCE;

    private ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
    private boolean consume = true;

    public void startConsuming(){
        while (consume) {
            System.out.println("NewUrisConsumingAndProcessing startConsuming 11");
            Optional<UriAndDepth> uriAndDepthOptional = NewUrlProcessingQueue.INSTANCE.getUriToProcess(); // If queue is empty getUriToProcess waits till timeout
            System.out.println("NewUrisConsumingAndProcessing startConsuming 12");
            if (uriAndDepthOptional.isPresent()){
                System.out.println("NewUrisConsumingAndProcessing startConsuming 13");
                executorService.submit(() -> NewUriProcessing.INSTANCE.process(uriAndDepthOptional.get()));
                System.out.println("NewUrisConsumingAndProcessing startConsuming 14");
            } else if (NewUriProcessingCountTracing.INSTANCE.processingIsDone()){
                System.out.println("NewUrisConsumingAndProcessing startConsuming 15");
                consume = false;
                System.out.println("NewUrisConsumingAndProcessing startConsuming 16");
                executorService.shutdown();
                System.out.println("NewUrisConsumingAndProcessing startConsuming 17");
            }
        }
    }

}
