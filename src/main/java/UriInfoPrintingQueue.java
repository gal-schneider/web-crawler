import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public enum UriInfoPrintingQueue {
    INSTANCE;

    private final BlockingQueue<UriInformation> uriInfoToPrint = new LinkedBlockingQueue<>();

    public void add(UriInformation uriInformation){
        uriInfoToPrint.add(uriInformation);
    }

    public List<UriInformation> get(int upToSize){
        List<UriInformation> uriInformationList = new ArrayList<>();
        while (!uriInfoToPrint.isEmpty() && uriInformationList.size() < upToSize) {
            UriInformation polled = uriInfoToPrint.poll();
            uriInformationList.add(polled);
        }
        return uriInformationList;
    }
}
