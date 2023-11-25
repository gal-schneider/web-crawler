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
        main1(new String[]{"https://www.google.com", "4"});
        System.out.println("Took:" + Duration.between(startTime, LocalDateTime.now()));
    }

    public static void main1(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();

        UriAndDepth uriAndDepth = validateAndGet(args);
        System.out.println("a1");
        NewUriProcessing.INSTANCE.setMaxDepth(uriAndDepth.depth());
        System.out.println("a2");
        NewUrlProcessingQueue.INSTANCE.addUrl(uriAndDepth.uri(), 1);
        System.out.println("a3");
        UrisWriter.INSTANCE.startPrinting();
        System.out.println("a4");
        Future<Boolean> processingEnded = executorService.submit(() -> {
            while (NewUrlProcessingQueue.INSTANCE.stillProcessing()){
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }
//            while (!NewUriProcessingCountTracing.INSTANCE.processingIsDone()) {
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//            }
            return true;
        });
        processingEnded.get();
        UrisWriter.INSTANCE.shutdown();
        NewUrlProcessingQueue.INSTANCE.shutDown();

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
