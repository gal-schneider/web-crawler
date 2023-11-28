import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public enum ParallelPageFetcher {
    INSTANCE;

    private static final HttpClient httpClient = HttpClient.newHttpClient();

    public String getPageSource(URI uri) {
        HttpRequest request = HttpRequest.newBuilder(uri).build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            StringBuilder stringBuilder = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(response.body().getBytes())));

            String line;
            while ((line = reader.readLine()) != null && !Thread.currentThread().isInterrupted()) {
                stringBuilder.append(line).append("\n");
            }

            return stringBuilder.toString();
        } catch (IOException | InterruptedException e) {
            ProcessingErrors.INSTANCE.add(uri, e, "ParallelPageFetcher.getPageSource");
            throw new RuntimeException(e);
        }
    }

}