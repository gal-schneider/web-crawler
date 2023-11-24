import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public enum NewUrisConsumingAndProcessing {
    INSTANCE;

    private ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
    private boolean consume = true;

    public void startConsuming(){
        while (consume) {
            Optional<UriAndDepth> uriAndDepthOptional = NewUrlProcessingQueue.INSTANCE.getUriToProcess(); // If queue is empty getUriToProcess waits till timeout
            if (uriAndDepthOptional.isPresent()){
                executorService.submit(() -> NewUriProcessing.INSTANCE.process(uriAndDepthOptional.get()));
            } else if (NewUriProcessingCountTracing.INSTANCE.processingIsDone()){
                consume = false;
                executorService.shutdown();
            }
        }
    }

}
