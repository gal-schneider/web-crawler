import org.apache.commons.lang3.math.NumberUtils;

import java.net.URI;
import java.util.Optional;

public class WebCrawlerMain {
    public static void main(String[] args){
        main1(new String[]{"https://www.google.com", "2"});
    }

    public static void main1(String[] args) {
        UriAndDepth uriAndDepth = validateAndGet(args);
        NewUrisConsumingAndProcessing.INSTANCE.startConsuming();
        WebCrawler webCrawler = new WebCrawler();
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
