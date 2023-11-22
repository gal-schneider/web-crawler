import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebCrawler {

    private static final Map<String, UrlInformation> urlToInfo = new HashMap<>();

    public static void main(String[] args){
        main1(new String[]{"https://www.google.com", "2"});
    }

    public static void main1(String[] args) {
        validateArgs(args);
        String url = args[0];
        int depth = Integer.parseInt(args[1]);
        collectContainedLinks(url, 1, depth);
        printContainedLinks();

    }

    public static void collectContainedLinks(String url, int currentDepth, int requiredDepth){
        if (urlToInfo.get(url) == null){
            List<String> containedUrls = WebPage.getPageContainedUrls(url);

            double rank = calculateRank(url, containedUrls);
            urlToInfo.put(url, new UrlInformation(currentDepth, rank));

            if (currentDepth < requiredDepth){
                for (String containedUrl: containedUrls) {
                    collectContainedLinks(containedUrl, currentDepth + 1, requiredDepth);
                }
            }
        }
    }

    private static void printContainedLinks(){
        System.out.println("a");
    }

    private static double calculateRank(String url, List<String> containedUrls){
        return 1.2;
    }

    private static void validateArgs(String[] args) {
        if (StringUtils.isEmpty(args[0])) {
            System.out.println("Url is missing");
            throw new IllegalArgumentException("Url parameter is missing");
        }

        if (StringUtils.isEmpty(args[1])) {
            throw new IllegalArgumentException("Depth parameter is missing");
        }

        if (!NumberUtils.isCreatable(args[1])) {
            throw new IllegalArgumentException("Depth parameter " + args[1] +  " is not a number");
        }
    }

}