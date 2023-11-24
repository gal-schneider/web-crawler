import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public enum UriInfoPrintingQueue {
    INSTANCE;

    BlockingQueue<UriInformation> uriInfoToPrint = new LinkedBlockingQueue<>();

    public void add(UriInformation uriInformation){
        uriInfoToPrint.add(uriInformation);
    }

    public List<UriInformation> get(int upToSize){
        List<UriInformation> uriInformations = new ArrayList<>();
        while (!uriInfoToPrint.isEmpty() && uriInformations.size() < upToSize) {
            uriInformations.add(uriInfoToPrint.poll());
        }
        return uriInformations;
    }
}
