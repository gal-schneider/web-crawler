import java.net.URI;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public enum NewUriProcessing {
    INSTANCE;

    private int maxDepth;
    private final AtomicInteger urisTotal = new AtomicInteger();
    private final AtomicInteger urisProcessed = new AtomicInteger();
    private final AtomicInteger errors = new AtomicInteger();

    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    public void process(UriAndDepth uriAndDepth){
        urisTotal.incrementAndGet();
        try {
            List<URI> containedUris = WebPage.getPageContainedUrls(uriAndDepth.uri());
            double rank = RankCalculator.INSTANCE.calculate(uriAndDepth.uri(), containedUris);
            if (uriAndDepth.depth() < maxDepth) {
                containedUris.forEach(uri -> NewUrlProcessingQueue.INSTANCE.addUrl(uri, uriAndDepth.depth() + 1));
            }
            UriInfoPrintingQueue.INSTANCE.add(new UriInformation(uriAndDepth.uri(), uriAndDepth.depth(), rank));
            urisProcessed.incrementAndGet();
        } catch (Exception ex){
            errors.incrementAndGet();
            ProcessingErrors.INSTANCE.add(uriAndDepth.uri(), ex, "NewUriProcessing.process");
        }
        System.out.println("Out of " + urisTotal.get() + ", processed " + urisProcessed.get() +  ", error " + errors.get());
    }
}
