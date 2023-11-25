import java.net.URI;
import java.util.Map;
import java.util.concurrent.*;

public enum NewUrlProcessingQueue {
    INSTANCE;

    private ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(1);
    private ExecutorService executor = Executors.newFixedThreadPool(500);
    private BlockingQueue<UriAndFuture> processingFutures = new LinkedBlockingQueue<>();

    private final Map<URI, Boolean> uriToDummyBoolean = new ConcurrentHashMap<>();
//    private final BlockingQueue<UriAndDepth> urisToProcess = new LinkedBlockingQueue<>();

    NewUrlProcessingQueue(){
        scheduledExecutor.scheduleWithFixedDelay(() -> {
            System.out.println("\u001B[34m NewUrlProcessingQueue removing futures \u001B[0m");
//            UriAndFuture poll = processingFutures.poll();
//            if (poll == null){
//                System.out.println("\u001B[34m NewUrlProcessingQueue q is empty \u001B[0m");
//            } else {
//                System.out.println("\u001B[34m NewUrlProcessingQueue poll=" + poll + ", poll.future.isDone()=" + poll.future.isDone() + "\u001B[0m");
//            }
              processingFutures.removeIf(uriAndFuture -> uriAndFuture.future.isDone() && uriAndFuture.future.isCompletedExceptionally());
        }, 10, 15, TimeUnit.SECONDS);
    }

    public void addUrl(URI uri, int depth){
        System.out.println("NewUrlProcessingQueue addUrl 1 uri=" + uri);
        uriToDummyBoolean.computeIfAbsent(uri, uriKey -> pushUriToTheToProcessQueueAndGetTheInfo(uriKey, depth));
    }

    private boolean pushUriToTheToProcessQueueAndGetTheInfo(URI uri, int depth) {
        NewUriProcessingCountTracing.INSTANCE.processingStart(uri, depth);
        System.out.println("PPP NewUrlProcessingQueue pushUriToTheToProcessQueueAndGetTheInfo 0 uri=" + uri);
        System.out.println("NewUrlProcessingQueue pushUriToTheToProcessQueueAndGetTheInfo 1");
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            NewUriProcessing.INSTANCE.process(new UriAndDepth(uri, depth));
        }, executor);
        processingFutures.add(new UriAndFuture(uri, future));
//        processingFutures.add(new UriAndFuture(uri, executor.submit(() -> NewUriProcessing.INSTANCE.process(new UriAndDepth(uri, depth)))));
        System.out.println("NewUrlProcessingQueue pushUriToTheToProcessQueueAndGetTheInfo 2");
        return true;
    }

    public boolean stillProcessing(){
        System.out.println("\u001B[31m processingFutures=" + processingFutures + "\u001B[0m");
        return !processingFutures.isEmpty();
    }

    public void shutDown(){
        executor.shutdown();
        scheduledExecutor.shutdown();
    }

    private record UriAndFuture(URI uri, CompletableFuture<?> future){}
}
