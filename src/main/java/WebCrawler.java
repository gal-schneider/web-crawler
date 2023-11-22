import org.apache.commons.lang3.math.NumberUtils;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class WebCrawler {

    private static final Map<URI, UrlInformation> uriToInfo = new HashMap<>();

    public static void main(String[] args){
        main1(new String[]{"https://www.google.com", "2"});
    }

    public static void main1(String[] args) {
        InputParameters inputParameters = validateAndGet(args);
        collectContainedLinks(inputParameters.uri(), 1, inputParameters.depth(), null);
        printContainedLinks();
    }

    public static void collectContainedLinks(URI uri, int currentDepth, int requiredDepth, URI baseUri){
        uri = resolveRelative(uri, baseUri);
        List<URI> containedUrls = WebPage.getPageContainedUrls(uri);

        double rank = calculateRank(uri, containedUrls);
        uriToInfo.put(uri, new UrlInformation(currentDepth, rank));

        if (currentDepth < requiredDepth){
            for (URI containedUri: containedUrls) {
                collectContainedLinks(containedUri, currentDepth + 1, requiredDepth, uri);
            }
        }
    }

    private static URI resolveRelative(URI uri, URI baseURI){
        if (uri.isAbsolute()) {
            return uri;
        }
        return baseURI.resolve(uri);
    }

    private static void printContainedLinks(){
        System.out.println("urlToInfo=" + uriToInfo);
    }

    private static double calculateRank(URI uri, List<URI> containedUrls){
        return 1.2;
    }

    private static InputParameters validateAndGet(String[] args) {
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

        return new InputParameters(uriOptional.get(), Integer.parseInt(args[1]));
    }

}