import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Predicate;

public enum NewUrlProcessingQueue {
    INSTANCE;

    private final static Predicate<UriAndFuture> FUTURE_IS_STUCK_PREDICATE = (uriAndFuture) ->  LocalDateTime.now().isAfter(uriAndFuture.startTime.plusMinutes(3));

    private final ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(1);
    private final ExecutorService executor =  Executors.newFixedThreadPool(100); // Executors.newVirtualThreadPerTaskExecutor();
    private final BlockingQueue<UriAndFuture> processingFutures = new LinkedBlockingQueue<>();
    private final Map<URI, Boolean> processingUriToDummyBoolean = new ConcurrentHashMap<>();
    private int killedStuckUris = 0;

    NewUrlProcessingQueue(){
        scheduledExecutor.scheduleWithFixedDelay(() -> {
            System.out.println("\u001B[34m NewUrlProcessingQueue removing futures \u001B[0m");
            processingFutures.removeIf(uriAndFuture -> !uriAndFuture.future.state().equals(Future.State.RUNNING));
            KillStuckUris();
        }, 4, 3, TimeUnit.SECONDS);
    }


    public void addUrl(URI uri, int depth){
        processingUriToDummyBoolean.computeIfAbsent(uri, uriKey -> pushUriToTheToProcessQueueAndGetTheInfo(uriKey, depth));
    }


    public boolean stillProcessing(){
        System.out.println("\u001B[31m processingFutures size=" + processingFutures.size() + "\u001B[0m");
        return !processingFutures.isEmpty();
    }

    public void shutDown(){
        executor.shutdownNow();
        scheduledExecutor.shutdownNow();
    }

    private boolean pushUriToTheToProcessQueueAndGetTheInfo(URI uri, int depth) {
        UriAndDepth uriAndDepth = new UriAndDepth(uri, depth);
//        CompletableFuture<Void> future = CompletableFuture.runAsync(() ->  NewUriProcessing.INSTANCE.process(uriAndDepth), executor);
        Future<?> future = executor.submit(() ->  NewUriProcessing.INSTANCE.process(uriAndDepth));
        processingFutures.add(new UriAndFuture(uriAndDepth, future));
        return true;
    }

    private void KillStuckUris(){
        List<UriAndFuture> stuckFutures = processingFutures.stream()
                .filter(FUTURE_IS_STUCK_PREDICATE)
                .toList();
        killedStuckUris = killedStuckUris + stuckFutures.size();
        removeAllStuckFuturesFromProcessingUriMap(stuckFutures);
        removeAllStuckFuturesFromFuturesQueue();
    }


    private void removeAllStuckFuturesFromFuturesQueue() {
        System.out.println("\u001B[31m Killed stuck urls: " + killedStuckUris + "\u001B[0m");
        processingFutures.removeIf(NewUrlProcessingQueue.FUTURE_IS_STUCK_PREDICATE);
    }

    private void removeAllStuckFuturesFromProcessingUriMap(List<UriAndFuture> stuckFutures) {
        stuckFutures.forEach(uriAndFuture ->
                processingUriToDummyBoolean.remove(uriAndFuture.uriAndDepth.uri())
        );
    }

    private static class UriAndFuture{
        private final UriAndDepth uriAndDepth;
        private final Future<?> future;
        private final LocalDateTime startTime = LocalDateTime.now();

        public UriAndFuture(UriAndDepth uriAndDepth, Future<?> future) {
            this.uriAndDepth = uriAndDepth;
            this.future = future;
        }

    }
}
