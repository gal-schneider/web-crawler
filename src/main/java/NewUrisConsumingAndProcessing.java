import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public enum NewUrisConsumingAndProcessing implements NewUrlProcessingQueueObserver{
    INSTANCE;

    private ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
    private boolean consume = true;

    NewUrisConsumingAndProcessing(){
        NewUrlProcessingQueue.INSTANCE.addObserver(this);
    }

    public void startConsuming(){
        while (consume && NewUrlProcessingQueue.INSTANCE.hasNewUri()) {
            UriAndDepth uriAndDepth = NewUrlProcessingQueue.INSTANCE.getUriToProcess();
            executorService.submit(() -> NewUriProcessing.INSTANCE.process(uriAndDepth));
        }
    }

    public void stopConsumers(){
        consume = false;
        executorService.shutdown();
    }

    private void NewUrisConsumingAndProcessing(){
        while (true) {
            UriAndDepth uriAndDepth = NewUrlProcessingQueue.INSTANCE.getUriToProcess();
            executorService.submit(() -> NewUriProcessing.INSTANCE.process(uriAndDepth));
        }
    }

    @Override
    public void newUrlArrived() {
        startConsuming();
    }
}
