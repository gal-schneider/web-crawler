import java.net.URI;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

public enum NewUriProcessingCountTracing {
    INSTANCE;
    private final AtomicInteger urisInProcessCount = new AtomicInteger();

    public void processingStart(URI uri, int depth){
        System.out.println("KKK +++ NewUriProcessingCountTracing processingStart 1 uri=" + uri + ", depth=" + depth);
        urisInProcessCount.incrementAndGet();
        System.out.println("KKK +++ NewUriProcessingCountTracing processingStart 2 after uri=" + uri + ", depth=" + depth);
    }

    public void processingEnd(URI uri){
        System.out.println("KKK --- NewUriProcessingCountTracing processingEnd 1 uri=" + uri + ", urisInProcessCount=" + urisInProcessCount.get());
        urisInProcessCount.decrementAndGet();
    }

    public boolean processingIsDone(){
        System.out.println("\\u001B[32mKKK ^^^ is done urisInProcessCount.getAcquire()=" + urisInProcessCount.getAcquire() + " time=" + LocalDateTime.now().getNano() + "\u001B[0m");
        ProcessingErrors.INSTANCE.getAll().forEach((uri, ex) -> System.out.println("\u001B[31m!!!!!!!!!!!!!! error in uri=" + uri + ", ex=" + ex + "\u001B[0m"));
        return urisInProcessCount.getAcquire() == 0;
    }

}
