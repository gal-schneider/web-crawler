import java.net.URI;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public enum ProcessingErrors {
    INSTANCE;

    private ConcurrentMap<URI, Exception> errors = new ConcurrentHashMap<>();

    public void add(URI uri, Exception ex){
        errors.put(uri, ex);
    }

    public ConcurrentMap<URI, Exception> getAll(){
        return errors;
    }
}
