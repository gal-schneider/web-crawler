import java.net.URI;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public enum NewUrlProcessingQueue {
    INSTANCE;

    private final Map<URI, Boolean> uriToDummyBoolean = new ConcurrentHashMap<>();
    private final BlockingQueue<UriAndDepth> urisToProcess = new LinkedBlockingQueue<>();
    public void addUrl(URI uri, int depth){
        uriToDummyBoolean.computeIfAbsent(uri, uriKey -> pushUriToTheToProcessQueueAndGetTheInfo(uriKey, depth));
    }

    public Optional<UriAndDepth> getUriToProcess(){
        try {
            return Optional.ofNullable(urisToProcess.poll(3, TimeUnit.MILLISECONDS));
        } catch (InterruptedException e) {
            // !!! Write to errors List
            throw new IllegalStateException(e);
        }
    }


    private boolean pushUriToTheToProcessQueueAndGetTheInfo(URI uri, int depth) {
        NewUriProcessingCountTracing.INSTANCE.processingStart();
        try {
            urisToProcess.put(new UriAndDepth(uri, depth));
        } catch (InterruptedException e) {
            /// !!!!!!!!!!! to add writing to error list;
            NewUriProcessingCountTracing.INSTANCE.processingEnd();
            throw new IllegalStateException("urisToProcess queue is full, there should be enough consumers to empty it", e);
        }
        return true;
    }
}
