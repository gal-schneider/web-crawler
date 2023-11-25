import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Predicate;

public enum NewUrlProcessingQueue {
    INSTANCE;

    private final ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(1);
    private final ExecutorService executor = Executors.newFixedThreadPool(1000); //Executors.newVirtualThreadPerTaskExecutor(); // Executors.newFixedThreadPool(500);
    private final BlockingQueue<UriAndFuture> processingFutures = new LinkedBlockingQueue<>();

    private final Map<URI, Boolean> processingUriToDummyBoolean = new ConcurrentHashMap<>();
//    private final BlockingQueue<UriAndDepth> urisToProcess = new LinkedBlockingQueue<>();

    NewUrlProcessingQueue(){
        scheduledExecutor.scheduleWithFixedDelay(() -> {
            System.out.println("\u001B[34m NewUrlProcessingQueue removing futures \u001B[0m");
            processingFutures.removeIf(uriAndFuture -> !uriAndFuture.future.state().equals(Future.State.RUNNING));
            rerunUriForStuckFutures();
        }, 4, 3, TimeUnit.SECONDS);
    }


    public void addUrl(URI uri, int depth){
//        System.out.println("NewUrlProcessingQueue addUrl 1 uri=" + uri);
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
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            NewUriProcessing.INSTANCE.process(uriAndDepth);
        }, executor);
        processingFutures.add(new UriAndFuture(uriAndDepth, future));
        return true;
    }

    private void rerunUriForStuckFutures(){
        Predicate<UriAndFuture> futureIsStuckPredicate = (uriAndFuture) ->  LocalDateTime.now().isAfter(uriAndFuture.startTime.plusMinutes(6));
        List<UriAndFuture> stuckFutures = processingFutures.stream()
                .filter(futureIsStuckPredicate)
                .toList();
        System.out.println("\u001B[31m stuckFutures size=" + stuckFutures.size() + "\u001B[0m");
        cancelAllStuckFutures(stuckFutures);
        removeAllStuckFuturesFromProcessingUriMap(stuckFutures);
        removeAllStuckFuturesFromFuturesQueue(futureIsStuckPredicate);
        addUrisOfStuckFuturesAgain(stuckFutures);
    }

    private void addUrisOfStuckFuturesAgain(List<UriAndFuture> stuckFutures) {
        stuckFutures.forEach(uriAndFuture -> addUrl(uriAndFuture.uriAndDepth.uri(), uriAndFuture.uriAndDepth.depth()));
    }

    private void removeAllStuckFuturesFromFuturesQueue(Predicate<UriAndFuture> futureIsStuckPredicate) {
        processingFutures.removeIf(futureIsStuckPredicate);
    }

    private void removeAllStuckFuturesFromProcessingUriMap(List<UriAndFuture> stuckFutures) {
        stuckFutures.forEach(uriAndFuture ->
                processingUriToDummyBoolean.remove(uriAndFuture.uriAndDepth.uri())
        );
    }

    private static void cancelAllStuckFutures(List<UriAndFuture> stuckFutures) {
        stuckFutures.forEach(uriAndFuture -> uriAndFuture.future.cancel(true));
    }

    private static class UriAndFuture{
        private final UriAndDepth uriAndDepth;
        private final CompletableFuture<?> future;
        private final LocalDateTime startTime = LocalDateTime.now();

        public UriAndFuture(UriAndDepth uriAndDepth, CompletableFuture<?> future) {
            this.uriAndDepth = uriAndDepth;
            this.future = future;
        }

        public UriAndDepth getUriAndDepth() {
            return uriAndDepth;
        }

        public CompletableFuture<?> getFuture() {
            return future;
        }

        public LocalDateTime getStartTime() {
            return startTime;
        }
    }
}
