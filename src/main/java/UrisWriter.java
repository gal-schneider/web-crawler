import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public enum UrisWriter {
    INSTANCE;

    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

    public void startPrinting(){
        scheduledExecutorService.scheduleWithFixedDelay(this::handleUriInfoBatchForWriting, 3, 1, TimeUnit.SECONDS);
    }

    public void shutdown(){
        scheduledExecutorService.shutdownNow();
        List<UriInformation> uriInformations = UriInfoPrintingQueue.INSTANCE.get(500);
        while (!uriInformations.isEmpty()){
            uriInformations.forEach(info -> System.out.println("info=" + info));
        }
    }

    private void handleUriInfoBatchForWriting(){
        UriInfoPrintingQueue.INSTANCE.get(500)
                .forEach(info -> UrisFileWriter.INSTANCE.addLine(info));
    }


}
