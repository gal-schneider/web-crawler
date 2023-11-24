import java.net.URI;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public enum RankCalculator {
    INSTANCE;

    private Lock lock = new ReentrantLock();
    private Condition notFull = lock.newCondition();
    private Condition notEmpty = lock.newCondition();


    public Lock getLock() {
        return lock;
    }

    public Condition getNotFull() {
        return notFull;
    }

    public Condition getNotEmpty() {
        return notEmpty;
    }

    public static double calculate(URI uri, List<URI> containedUrls){
        if (containedUrls.isEmpty()){
            return 0;
        }

        double numberOfElementsWithSameDomain = containedUrls.stream()
                .filter(containedUri -> isSameDomain(uri, containedUri))
                .toList().size();
        return numberOfElementsWithSameDomain / containedUrls.size();
    }

    private static boolean isSameDomain(URI uri1, URI uri2){
        return uri1.getHost().equalsIgnoreCase(uri2.getHost());
    }
}
