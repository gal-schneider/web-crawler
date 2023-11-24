import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public enum UrisWriter {
    INSTANCE;

    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

    public void startPrinting(){
        scheduledExecutorService.schedule(this::handleUriInfoBatchForWriting, 3, TimeUnit.MILLISECONDS);
    }

    public void shutdown(){
        scheduledExecutorService.shutdown();
    }

    private void handleUriInfoBatchForWriting(){
        UriInfoPrintingQueue.INSTANCE.get(50)
                .forEach(info -> System.out.println(info));
    }


}
