import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public enum UriInfoPrintingQueue {
    INSTANCE;

    BlockingQueue<UriInformation> uriInfoToPrint = new LinkedBlockingQueue<>();

    public void add(UriInformation uriInformation){
//        System.out.println(">>> +++ UriInfoPrintingQueue add uriInformation=" + uriInformation);
        uriInfoToPrint.add(uriInformation);
    }

    public List<UriInformation> get(int upToSize){
        List<UriInformation> uriInformationList = new ArrayList<>();
//        System.out.println(">>> --- UriInfoPrintingQueue get=" + uriInformationList);
        while (!uriInfoToPrint.isEmpty() && uriInformationList.size() < upToSize) {
            UriInformation polled = uriInfoToPrint.poll();
//            System.out.println(">>> --- UriInfoPrintingQueue 2 polled=" + polled);
            uriInformationList.add(polled);
        }
//        System.out.println(">>> --- UriInfoPrintingQueue 3");
        return uriInformationList;
    }
}
