import java.net.URI;
import java.util.List;

public enum NewUriProcessing {
    INSTANCE;

    public void process(UriAndDepth uriAndDepth){
        List<URI> containedUris = WebPage.getPageContainedUrls(uriAndDepth.uri());
        double rank = RankCalculator.INSTANCE.calculate(uriAndDepth.uri(), containedUris);
        containedUris.forEach(uri -> NewUrlProcessingQueue.INSTANCE.addUrl(uri, uriAndDepth.depth() + 1));
        UriInfoPrintingQueue.INSTANCE.add(new UriInformation(uriAndDepth.uri(), uriAndDepth.depth(), rank));
    }
}
