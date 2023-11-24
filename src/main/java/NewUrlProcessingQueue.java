import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public enum NewUrlProcessingQueue {
    INSTANCE;

    private final Map<URI, Boolean> uriToDummyBoolean = new ConcurrentHashMap<>();
    private final BlockingQueue<UriAndDepth> urisToProcess = new LinkedBlockingQueue<>();

    private final List<NewUrlProcessingQueueObserver> observers = new ArrayList<>();
    public void addUrl(URI uri, int depth){
        uriToDummyBoolean.computeIfAbsent(uri, uriKey -> pushUriToTheToProcessQueueAndGetTheInfo(uriKey, depth));
        observers.forEach(observer -> observer.newUrlArrived());
    }

    public void addObserver(NewUrlProcessingQueueObserver observer){
        observers.add(observer);
    }

    public boolean hasNewUri(){
        return !urisToProcess.isEmpty();
    }

    public UriAndDepth getUriToProcess(){
        try {
            return urisToProcess.take();
        } catch (InterruptedException e) {
            throw new IllegalStateException("urisToProcess queue is empty, no one should ask for a uri in this case", e);
        }
    }

    private boolean pushUriToTheToProcessQueueAndGetTheInfo(URI uri, int depth) {
        try {
            urisToProcess.put(new UriAndDepth(uri, depth));
        } catch (InterruptedException e) {
            throw new IllegalStateException("urisToProcess queue is full, there should be enough consumers to empty it", e);
        }
        return true;
    }
}
