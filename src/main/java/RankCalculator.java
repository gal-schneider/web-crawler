import java.net.URI;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public enum RankCalculator {
    INSTANCE;

    public double calculate(URI uri, List<URI> containedUrls){
        if (containedUrls.isEmpty()){
            return 0;
        }

        double numberOfElementsWithSameDomain = containedUrls.stream()
                .filter(containedUri -> isSameDomain(uri, containedUri))
                .toList().size();
        return numberOfElementsWithSameDomain / containedUrls.size();
    }

    private boolean isSameDomain(URI uri1, URI uri2){
        return uri1.getHost().equalsIgnoreCase(uri2.getHost());
    }
}
