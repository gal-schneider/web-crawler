import java.net.URI;
import java.util.List;

public enum NewUriProcessing {
    INSTANCE;

    private int maxDepth;

    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    public void process(UriAndDepth uriAndDepth){
        System.out.println("NewUriProcessing process 1");
        List<URI> containedUris = WebPage.getPageContainedUrls(uriAndDepth.uri());
        System.out.println("NewUriProcessing process 2 containedUris=" + containedUris);
        double rank = RankCalculator.INSTANCE.calculate(uriAndDepth.uri(), containedUris);
        System.out.println("NewUriProcessing process 3");
        if (uriAndDepth.depth() < maxDepth) {
            System.out.println("NewUriProcessing process 4");
            containedUris.forEach(uri -> NewUrlProcessingQueue.INSTANCE.addUrl(uri, uriAndDepth.depth() + 1));
        }
        System.out.println("NewUriProcessing process 5");
        UriInfoPrintingQueue.INSTANCE.add(new UriInformation(uriAndDepth.uri(), uriAndDepth.depth(), rank));
        System.out.println("NewUriProcessing process 6 uriAndDepth=" + uriAndDepth.uri());
        NewUriProcessingCountTracing.INSTANCE.processingEnd(uriAndDepth.uri());
        System.out.println("NewUriProcessing process 7");
    }
}
