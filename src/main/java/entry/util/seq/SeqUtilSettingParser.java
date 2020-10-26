package entry.util.seq;

import common.datastore.MinoOperationWithKey;
import common.datastore.Operations;
import common.datastore.SimpleMinoOperation;
import common.parser.OperationTransform;
import common.tetfu.Tetfu;
import common.tetfu.TetfuPage;
import common.tetfu.common.ColorConverter;
import common.tetfu.common.ColorType;
import common.tetfu.field.ColoredField;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.mino.Piece;
import entry.CommandLineWrapper;
import entry.common.Loader;
import entry.common.SettingParser;
import entry.path.PathOptions;
import exceptions.FinderParseException;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SeqUtilSettingParser extends SettingParser<SeqUtilSettings> {
    private static final String CHARSET_NAME = "utf-8";
    private static final String DEFAULT_PATTERNS_TXT = "input/patterns.txt";
    private static final String DEFAULT_FIELD_TXT = "input/field.txt";
    private static final String PATTERN_DELIMITER = ";";

    public SeqUtilSettingParser(Options options, CommandLineParser parser) {
        super(options, parser);
    }

    @Override
    protected Optional<SeqUtilSettings> parse(CommandLineWrapper wrapper) throws FinderParseException {
        SeqUtilSettings settings = new SeqUtilSettings();

        MinoFactory minoFactory = new MinoFactory();
        ColorConverter colorConverter = new ColorConverter();

        // テト譜の読み込み
        List<String> fumens = loadFumenData(
                wrapper,
                SeqUtilOptions.Fumen.optName(),
                SeqUtilOptions.FieldPath.optName(),
                DEFAULT_FIELD_TXT,
                Charset.forName(CHARSET_NAME)
        ).stream()
                .map(Tetfu::removeDomainData)
                .filter(Tetfu::isDataLater115)
                .map(Tetfu::removePrefixData)
                .collect(Collectors.toList());

        if (fumens.isEmpty()) {
            throw new FinderParseException("Cannot load fumen" + fumens);
        }

        ArrayList<SeqUtilParameter> parameters = new ArrayList<>();

        for (String input : fumens) {
            int start = wrapper.getIntegerOption(SeqUtilOptions.StartPage.optName()).orElse(1);
            int end = wrapper.getIntegerOption(SeqUtilOptions.EndPage.optName()).orElse(-1);

            String data = input;

            if (data.contains("#")) {
                String[] dataPage = data.split("#");
                data = dataPage[0];

                if (dataPage[1].contains(":")) {
                    String[] startEnd = dataPage[1].split(":");
                    if (!startEnd[0].isEmpty()) {
                        start = Integer.parseInt(startEnd[0]);
                    }

                    if (!startEnd[1].isEmpty()) {
                        end = Integer.parseInt(startEnd[1]);
                    }
                }
            }

            Tetfu tetfu = new Tetfu(minoFactory, colorConverter);
            List<TetfuPage> pages = tetfu.decode(data);

            if (start < 1) {
                throw new FinderParseException("Start page should be greater than 0");
            }

            if (pages.size() < start) {
                throw new FinderParseException("Start page should be less than or equal to " + pages.size());
            }

            if (end == -1) {
                end = pages.size();
            }

            if (end < start) {
                throw new FinderParseException("End page should be greater than or equal to start page");
            }

            if (pages.size() < end) {
                throw new FinderParseException("End page should be less than or equal to " + pages.size());
            }

            pages = pages.subList(start - 1, end);

            if (pages.isEmpty()) {
                throw new FinderParseException("Specified page is empty");
            }

            List<SimpleMinoOperation> operationList = pages.stream()
                    .filter(TetfuPage::isPutMino)
                    .map(page -> {
                        Piece piece = colorConverter.parseToBlock(page.getColorType());
                        Mino mino = minoFactory.create(piece, page.getRotate());
                        return new SimpleMinoOperation(mino, page.getX(), page.getY());
                    })
                    .collect(Collectors.toList());

            int height = 24;
            Field field = toField(pages.get(0).getField(), height);

            List<MinoOperationWithKey> operationsWithKey = OperationTransform.parseToOperationWithKeys(
                    field, new Operations(operationList), minoFactory, height
            );

            parameters.add(new SeqUtilParameter(input, data, field, operationsWithKey));
        }

        settings.setParameters(parameters);

        // パターンの読み込み
        List<String> patterns = Loader.loadPatterns(
                wrapper,
                PathOptions.Patterns.optName(),
                PATTERN_DELIMITER,
                PathOptions.PatternsPath.optName(),
                DEFAULT_PATTERNS_TXT,
                Charset.forName(CHARSET_NAME)
        );
        settings.setPatterns(patterns);

        // ドロップの設定
        Optional<String> dropType = wrapper.getStringOption(SeqUtilOptions.Drop.optName());
        try {
            dropType.ifPresent(type -> {
                String key = dropType.orElse("softdrop");
                try {
                    settings.setDropType(key);
                } catch (FinderParseException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (Exception e) {
            throw new FinderParseException("Unsupported format: format=" + dropType.orElse("<empty>"));
        }

        // ログファイルの設定
        Optional<String> logFilePath = wrapper.getStringOption(SeqUtilOptions.LogPath.optName());
        logFilePath.ifPresent(settings::setLogFilePath);

        // アウトプットファイルの設定
        Optional<String> outputBaseFilePath = wrapper.getStringOption(SeqUtilOptions.OutputBase.optName());
        outputBaseFilePath.ifPresent(settings::setOutputBaseFilePath);

        return Optional.of(settings);
    }

    // フィールドの情報を読み込む
    private static List<String> loadFumenData(
            CommandLineWrapper wrapper,
            String fumenOptName,
            String fieldPathOptName,
            String defaultFieldText,
            Charset charset
    ) throws FinderParseException {
        if (wrapper.hasOption(fumenOptName)) {
            // テト譜から
            return wrapper.getStringOptions(fumenOptName);
        } else {
            // フィールドファイルから
            Optional<String> fieldPathOption = wrapper.getStringOption(fieldPathOptName);
            String fieldPath = fieldPathOption.orElse(defaultFieldText);
            Path path = Paths.get(fieldPath);

            Stream<String> lines;
            try {
                lines = Files.lines(path, charset);
            } catch (IOException e) {
                throw new FinderParseException("Cannot open field file");
            }

            LinkedList<String> fieldLines = lines
                    .map(str -> {
                        if (str.contains("#"))
                            return str.substring(0, str.indexOf('#'));
                        return str;
                    })
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toCollection(LinkedList::new));

            if (fieldLines.isEmpty())
                throw new FinderParseException("Should specify clear-line & field-definition in field file");

            return fieldLines;
        }
    }

    private Field toField(ColoredField coloredField, int height) {
        Field field = FieldFactory.createField(height);
        for (int y = 0; y < height; y++)
            for (int x = 0; x < 10; x++)
                if (coloredField.getColorType(x, y) != ColorType.Empty)
                    field.setBlock(x, y);
        return field;
    }
}
