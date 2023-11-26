import org.apache.commons.lang3.math.NumberUtils;

import java.net.URI;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class WebCrawlerMain {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        LocalDateTime startTime = LocalDateTime.now();
        main1(new String[]{"https://www.google.com", "2"});
        System.out.println("Took:" + Duration.between(startTime, LocalDateTime.now()));
    }

    public static void main1(String[] args) throws ExecutionException, InterruptedException {

        UriAndDepth uriAndDepth = validateAndGet(args);
        NewUriProcessing.INSTANCE.setMaxDepth(uriAndDepth.depth());
        NewUrlProcessingQueue.INSTANCE.addUrl(uriAndDepth.uri(), 1);
        UrisWriter.INSTANCE.startPrinting();
        waitForProcessingToEnd();
        UrisWriter.INSTANCE.shutdown();
        NewUrlProcessingQueue.INSTANCE.shutDown();

    }

    private static void waitForProcessingToEnd() throws InterruptedException, ExecutionException {
        ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
        Future<Boolean> processingEnded = executorService.submit(() -> {
            while (NewUrlProcessingQueue.INSTANCE.stillProcessing()){
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }
            return true;
        });
        processingEnded.get();
    }

    private static UriAndDepth validateAndGet(String[] args) {
        if (args.length != 2){
            System.out.println("Need to supply URL and depth");
            throw new IllegalArgumentException("Need to supply URL and depth");
        }

        Optional<URI> uriOptional = UrlGenerator.create(args[0]);
        if (uriOptional.isEmpty()){
            System.out.println("Url is not valid");
            throw new IllegalArgumentException("Url " + args[0] + "parameter is not valid");
        }

        if (!uriOptional.get().isAbsolute()){
            System.out.println("Url is not absolute");
            throw new IllegalArgumentException("Url " + args[0] + "parameter is not absolute");
        }

        if (!NumberUtils.isCreatable(args[1])) {
            throw new IllegalArgumentException("Depth parameter " + args[1] +  " is not a number");
        }

        return new UriAndDepth(uriOptional.get(), Integer.parseInt(args[1]));
    }

}
