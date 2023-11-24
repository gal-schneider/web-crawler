import java.util.concurrent.atomic.AtomicInteger;

public enum NewUriProcessingCountTracing {
    INSTANCE;
    private final AtomicInteger urisInProcessCount = new AtomicInteger();

    public void processingStart(){
        urisInProcessCount.incrementAndGet();
    }

    public void processingEnd(){
        urisInProcessCount.decrementAndGet();
    }

    public boolean processingIsDone(){
        return urisInProcessCount.getAcquire() == 0;
    }

}
