import entry.EntryPointMain;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) {
        try {
            Stream<String> lines = Files.lines(Paths.get(args[0])).parallel();
            lines.filter(line -> {
                    int count = EntryPointMain.main(new String[]{"percent", "-t", line.strip(), "-p", "*!", "-fc", "0", "-th", "1"});
                    return count == 5040;
                })
                .forEach(System.out::println);
            lines.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
