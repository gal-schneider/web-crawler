import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public enum UrisFileWriter {
    INSTANCE;

    private Path path;

    private Path filePath;

    public void start(String filePath) {
        this.filePath = Path.of(filePath);
        String titleLine = "url depth ratio";

        try {
            Files.write(this.filePath, titleLine.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addLine(UriInformation info) {
        String line = info.uri() + " " + info.depth() + " " + Math.round(info.rank() * 100.0) / 100.0;
        if (filePath != null) {
            try {
                Files.write(filePath, ("\n" + line).getBytes(), StandardOpenOption.APPEND);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}