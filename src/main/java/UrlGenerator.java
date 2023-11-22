import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

public class UrlGenerator {
    public static Optional<URI> create(String pageUrl){
        try {
            return Optional.of( new URI(pageUrl));
        } catch (URISyntaxException e) {
            return Optional.empty();
        }
    }
}
