import entry.EntryPointMain;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import common.datastore.Pair;
import java.util.Iterator;

public class Main {
    public static void main(String[] args) {
        try {
            Stream<String> lines = Files.lines(Paths.get(args[0]));
            Stream<Integer> indexes = Stream.iterate(1, i -> i + 1);
            zip(indexes, lines).parallel().filter(line -> {
                    int count = EntryPointMain.main(new String[]{"percent", "-t", line.getValue().strip(), "-p", "*!", "-fc", "0", "-th", "1"});
                    return count == 5040;
                })
                .forEach(line -> System.out.println(line.getKey() + ": " + line.getValue()));
            lines.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static <T, V>  Stream<Pair<T,V>> zip(Stream<T> first, Stream<V> second ){
        Iterable<Pair<T,V>> iterable = () -> new ZippedIterator<>(first.iterator(), second.iterator());
        return StreamSupport.stream(iterable.spliterator(), false);
    }

    private static class ZippedIterator<K, V> implements Iterator<Pair<K, V>> {
        public final Iterator<K> first;
        public final Iterator<V> second;

        public ZippedIterator(Iterator<K> first, Iterator<V> second) {
            this.first = first;
            this.second = second;
        }

        @Override
        public Pair<K, V> next() {
            return new Pair<>(first.next(), second.next());
        }

        @Override
        public boolean hasNext() {
            return first.hasNext() && second.hasNext();
        }

    }
}
