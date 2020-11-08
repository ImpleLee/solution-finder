import entry.EntryPointMain;
import output.HTMLColumn;

import java.util.Optional;

public class DevMain {
    public static void main(String[] args) throws Exception {
//        String command = "path -f csv -k p -t v115@9gF8DeF8DeF8DeF8NeAgH -p T,*p4";
//        String command = "setup -t v115@Pg1hDe1hBeB8FtCeA8FtB8AeB8EtA8BeD8CtA8CeE8?AtB8AeI8AeG8JeAgH -p *p7 -m i -f z";

//        String command = "percent -t http://fumen.zui.jp/?v115@DhD8HeC8HeG8BeB8JeAgH -p L,L,S,J,J,T,O -fc 0 -td 1";
//        String command = "percent -t v115@wgF8FeG8CeB8GeC8DeF8GeB8JeAgH -p L,L,Z,S,Z,I,J,I,Z,T,O -c 7 --hold no";
//        String command = "percent -t v115@FfI8AeI8AeI8AeI8BeI8AeI8AeI8AeR8AeI8AeI8Ae?I8BeI8AeI8AeI8AeI8JeAgH -p III* -c 16";
//        String command = "move -t v115@9gA8IeA8IeA8IeA8SeAgH -p [TIO]p2";
//        String command = "path -k solution -f csv -t v115@zgyhGexhHexhGeAtxhC8BeA8BtyhE8AtA8JeAgWBAV?AAAAvhAAAPBAUAAAA -P 2 -p [IJLOS]p5,S --split yes";
//        String command = "path -t v115@9gB8HeC8GeE8EeF8NeAgWMA0no2ANI98AQPcQB";
//        String command = "path -t v115@9gB8EeF8DeG8CeF8DeC8JeAgH -p I,*p3";
//        String command = "path -t v115@QhC8BeA8BeE8AeG8JeAgH -p *!";

//         String command = "setup -p [^T]! --fill i --margin o -t v115@zgdpwhUpxhCe3hAe1hZpJeAgH";  // 14
//        String command = "setup -p [^T]! --fill i --margin o -t v115@zgTpwhYpAeUpzhAe3hQpAeQpzhTpAeUpJeAgH";  // 7
//        String command = "setup -p [^T]! --fill i --margin o -t v115@zgUpwhYpAeTp0hAe3hQpAeQpyhUpAeTpJeAgH";  // 7

//        String command = "setup -p [S]! --fill i --margin o -t v115@8gQpbexhGeQpwhKeAgH";  // -> S only = 1
//        String command = "setup -p [I]! --fill i --margin o -t v115@8gQpIeQpIeQpIeQpIeQpJeAgH";  // -> Last I only = 1
//        String command = "setup -p [I]! --fill i --margin o -t v115@8gQpIeQpIeQpIeQpIeQpJeAgH";  // -> all margin = error
//        String command = "setup -p [I]! --fill i --margin o -t v115@8gQpIeQpIeQpIeQpIewhJeAgH";  // -> Last I only h5 = 1
//        String command = "setup -p [I]! --fill i --margin o -t v115@GhQpIeQpIeQpIewhJeAgH";  // -> Last I only h4 = 1
//        String command = "setup -p [I]! --fill i --margin o -t v115@FhQpIeQpIeQpIewhKeAgH";  // -> x8 I only h4 = 1
//        String command = "setup -p [I]! --fill i --margin o -t v115@6gQpIeQpIeQpIeQpIewhLeAgH";  // -> x7 I only h5 = 1
//        String command = "path -s yes -c 6 -p [^IL]!,*p2 -t v115@wghlwhHeglwhEeA8BtglwhA8DeB8BtwhT8JeAgH";
//        String command = "path -t v115@VgL8FeC8AewwBeI8BeS8BeG8EeD8EeB8JeAgH -c 8 -p Z,T,Z,L,*p3 -f csv -k pattern -r true";

//        String command = "ren --tetfu v115@VgF8DeF8DeF8DeF8DeF8DeF8DeF8DeI8KeAgH --patterns TOLJISZ";
//        String command = "ren -t v115@neF8DeF8DeF8DeF8DeF8DeF8DeF8DeF8DeF8DeF8De?F8DeF8DeF8DeF8DeF8DeF8DeF8DeF8AeL8KeAgH -p tilsojztilsojz";

//        String command = "ren -h";

//        String command = "setup -p [^SZT]! --fill i --margin o -t v115@zgTpwhQpDeTpAeQpDezhAewhDeyhQpAeQpwhCeTpAe?RpMeAgH";
//        String command = "setup -p *! --fill i --margin o -t v115@3gwhHezhGe0hAexhAe4hAe2hJeAgH";
//        String command = "setup -p *! --fill i --margin o -t v115@2gWpCeWpDe0hQpxhAe4hAe2hJeAgH --format csv";
//        String command = "setup -p *! --fill i --margin o -t v115@2gWpCeWpDe0hQpxhAe4hAe2hJeAgH";
//        String command = "setup -p [^SZT]! --fill i --margin o -t v115@zgTpwhQpDeTpAeQpDezhAewhDeyhQpAexhCeTpAeRp?MeAgH";

//        String command = "path -t v115@RhA8GeE8EeB8JeAgH -p LZZSITSO";
//        String command = "path -p J,Z,O,S,L,I,I,J,S,O,Z -t v115@vhAAgH";

//        String command = "spin ";  // 0 solutions
        String command = "spin -p *! -t v115@HhB8AeH8BeI8AeG8JeAgH -mr 1";  // 2 solutions
//        String command = "spin -p *! -t v115@HhB8AeH8BeI8AeG8JeAgH -r no";  // 1 solutions

//        String command = "spin --tetfu v115@KhB8DeB8BeB8AeI8BeC8JeAgH --patterns *p7 -ft 7";
//        String command = "util fig --tetfu v115@BgC8GeC8BeglDeD8AeglDeE8hlCeF8ywAtG8wwBtH8?AtAeI8AeI8AeI8KeAgH -F png -f no";

//        String command = "util seq -t v115@vhFRQJUGJKJJvMJTNJGBJ v115@vhFRQJPGJKJJGMJTNJ0BJ -p *!";
//        String command = "util seq -t v115@vhFRQJUGJKJJvMJTNJGBJ#:-1 v115@vhFRQJPGJKJJGMJTNJ0BJ#1:5 -p *!";
//        String command = "util seq -t v115@vhERQJPGJKJJGMJTNJ -p *!";

//        String command = "util fumen -M reduce -t v115@vhdXKJNJJ0/ISSJzHJGDJJHJSGJvLJ0KJJEJzJJ+NJ?tMJFDJz/IyQJsGJOGJpFJ+NJ3MJXDJULJzGJxBJiJJtKJyJ?J0JJ";

        int returnCode = EntryPointMain.main(command.split(" "));
        System.exit(returnCode);

//        ExecutorService executorService = Executors.newFixedThreadPool(6);
//        try (AsyncBufferedFileWriter writer = new AsyncBufferedFileWriter(new File("output/test"), Charset.defaultCharset(), false, 10L)) {
//            for (int i = 0; i < 10; i++) {
//                int finalI = i;
//                executorService.submit(() -> {
//                    for (int j = 0; j < 10; j++) {
//                        writer.writeAndNewLine(String.format("hello %d-%03d", finalI, j));
//                    }
//                });
//            }
//            executorService.shutdown();
//            executorService.awaitTermination(100L, TimeUnit.SECONDS);
//        }
//        LoadedPatternGenerator generator = new LoadedPatternGenerator("TISZ*![tisz]");
//        int depth = generator.getDepth();
//        System.out.println(depth);
//        generator.blocksStream().map(Pieces::getPieces).forEach(System.out::println);

//        HTMLBuilder<TestColumn> builder = new HTMLBuilder<>("hello");
//        builder.addColumn(TestColumn.SECTION1, "hello");
//        builder.addColumn(TestColumn.SECTION2, "test1");
//        builder.addColumn(TestColumn.SECTION1, "world");
//        builder.addColumn(TestColumn.SECTION2, "test2");
//        List<String> list = builder.toList(Arrays.asList(TestColumn.SECTION1, TestColumn.SECTION2), true);
//
//        StringBuilder builder1 = new StringBuilder();
//        String lineSeparator = System.lineSeparator();
//        for (String s : list) {
//            builder1.append(s).append(lineSeparator);
//        }
//        System.out.println(builder1.toString());

    }

    public enum TestColumn implements HTMLColumn {
        SECTION1,
        SECTION2;

        @Override
        public String getTitle() {
            return name();
        }

        @Override
        public String getId() {
            return name();
        }

        @Override
        public Optional<String> getDescription() {
            return Optional.empty();
        }
    }
}
