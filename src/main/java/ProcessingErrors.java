import java.net.URI;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public enum ProcessingErrors {
    INSTANCE;

//    private ConcurrentMap<URI, Exception> errors = new ConcurrentHashMap<>();
    List<Error> errors = new CopyOnWriteArrayList<>();

    public void add(URI uri, Exception ex, String message){
        errors.add(new Error(uri, ex, message));
    }

    public void printAll(){
        errors.forEach(System.out::println);
    }

    private record Error(URI uri, Exception ex, String message){}
}
