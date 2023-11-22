import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.net.URI;

import java.net.URLConnection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


public class WebPage {

    public static List<URI> getPageContainedUrls(URI uri) {
        try {
            return findUrls(sourceToDocument(getPageSource(uri)));
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }

    private static String getPageSource(URI uri) throws IOException {
        URLConnection connection = uri.toURL().openConnection();

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

    private static List<URI> findUrls(Document document){
        return document.select("a").stream()
                .map(link -> link.attr("href"))
                .map(UrlGenerator::create)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

}
