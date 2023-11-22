import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


public class WebPage {

    public static List<String> getPageContainedUrls(String pageUrl){
        try {
//            URL url = new URL(urlString);
            return findUrls(sourceToDocument(getPageSource(pageUrl)));
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    private static String getPageSource(String urlString) throws IOException {
        URL url = new URL(urlString);

        URLConnection connection = url.openConnection();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder stringBuilder = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }

            return stringBuilder.toString();
        }
    }

    private static Document sourceToDocument(String htmlContent){
            return Jsoup.parse(htmlContent);
    }

    private static List<String> findUrls(Document document){
        return document.select("a").stream()
                .map(link -> link.attr("href"))
                .toList();
    }

}
