import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public enum UrisWriter {
    INSTANCE;

    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

    public void startPrinting(){
        System.out.println(">>> >>> UrisWriter startPrinting 1");
        scheduledExecutorService.scheduleWithFixedDelay(this::handleUriInfoBatchForWriting, 3, 1, TimeUnit.SECONDS);
    }

    public void shutdown(){
        System.out.println(">>> >>> UrisWriter shutdown 1");
        scheduledExecutorService.shutdown();
    }

    private void handleUriInfoBatchForWriting(){
        System.out.println(">>> >>> UrisWriter handleUriInfoBatchForWriting 1");

        UriInfoPrintingQueue.INSTANCE.get(500)
                .forEach(info -> System.out.println("info=" + info));
    }


}
