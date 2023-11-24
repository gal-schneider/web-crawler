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
        System.out.println("NewUrlProcessingQueue addUrl 1 uri=" + uri);
        uriToDummyBoolean.computeIfAbsent(uri, uriKey -> pushUriToTheToProcessQueueAndGetTheInfo(uriKey, depth));
    }

    public Optional<UriAndDepth> getUriToProcess(){
        try {
            System.out.println("NewUrlProcessingQueue getUriToProcess 1");
            return Optional.ofNullable(urisToProcess.poll(3, TimeUnit.MILLISECONDS));
        } catch (InterruptedException e) {
            System.out.println("!!! NewUrlProcessingQueue getUriToProcess 2");
            // !!! Write to errors List
            throw new IllegalStateException(e);
        }
    }


    private boolean pushUriToTheToProcessQueueAndGetTheInfo(URI uri, int depth) {
       System.out.println("PPP NewUrlProcessingQueue pushUriToTheToProcessQueueAndGetTheInfo 0 uri=" + uri);
       NewUriProcessingCountTracing.INSTANCE.processingStart(uri, depth);
        try {
            System.out.println("NewUrlProcessingQueue pushUriToTheToProcessQueueAndGetTheInfo 1");
            urisToProcess.put(new UriAndDepth(uri, depth));
            System.out.println("NewUrlProcessingQueue pushUriToTheToProcessQueueAndGetTheInfo 2");
        } catch (InterruptedException e) {
            /// !!!!!!!!!!! to add writing to error list;
            NewUriProcessingCountTracing.INSTANCE.processingEnd(uri);
            throw new IllegalStateException("PPP urisToProcess queue is full, there should be enough consumers to empty it uri=" + uri, e);
        }
        return true;
    }
}
