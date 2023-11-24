import java.net.URI;
import java.util.concurrent.atomic.AtomicInteger;

public enum NewUriProcessingCountTracing {
    INSTANCE;
    private final AtomicInteger urisInProcessCount = new AtomicInteger();

    public void processingStart(URI uri, int depth){
        System.out.println("KKK +++ NewUriProcessingCountTracing processingStart 1 uri=" + uri + ", depth=" + depth);
        urisInProcessCount.incrementAndGet();
    }

    public void processingEnd(URI uri){
        System.out.println("KKK --- NewUriProcessingCountTracing processingEnd 1 uri=" + uri + ", urisInProcessCount=" + urisInProcessCount.get());
        urisInProcessCount.decrementAndGet();
    }

    public boolean processingIsDone(){
        return urisInProcessCount.getAcquire() == 0;
    }

}
