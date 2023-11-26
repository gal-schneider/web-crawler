import java.net.URI;
import java.util.List;

public enum NewUriProcessing {
    INSTANCE;

    private int maxDepth;

    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    public void process(UriAndDepth uriAndDepth){
        try {
            List<URI> containedUris = WebPage.getPageContainedUrls(uriAndDepth.uri());
            double rank = RankCalculator.INSTANCE.calculate(uriAndDepth.uri(), containedUris);
            if (uriAndDepth.depth() < maxDepth) {
                System.out.println("NewUriProcessing process 4");
                containedUris.forEach(uri -> NewUrlProcessingQueue.INSTANCE.addUrl(uri, uriAndDepth.depth() + 1));
            }
            UriInfoPrintingQueue.INSTANCE.add(new UriInformation(uriAndDepth.uri(), uriAndDepth.depth(), rank));
        } catch (Exception ex){
            ProcessingErrors.INSTANCE.add(uriAndDepth.uri(), ex);
        }
    }
}
