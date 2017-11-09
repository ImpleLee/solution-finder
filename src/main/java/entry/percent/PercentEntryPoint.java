package entry.percent;

import common.datastore.Pair;
import common.datastore.action.Action;
import common.datastore.blocks.LongPieces;
import common.datastore.blocks.Pieces;
import common.pattern.PatternGenerator;
import common.tree.AnalyzeTree;
import concurrent.HarddropCandidateThreadLocal;
import concurrent.LockedCandidateThreadLocal;
import concurrent.LockedNeighborCandidateThreadLocal;
import core.FinderConstant;
import core.action.candidate.Candidate;
import core.field.Field;
import core.field.FieldView;
import entry.DropType;
import entry.EntryPoint;
import entry.Verify;
import entry.path.output.MyFile;
import entry.searching_pieces.NormalEnumeratePieces;
import exceptions.FinderException;
import exceptions.FinderExecuteException;
import exceptions.FinderInitializeException;
import exceptions.FinderTerminateException;
import lib.Stopwatch;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class PercentEntryPoint implements EntryPoint {
    private static final String LINE_SEPARATOR = System.lineSeparator();

    private final PercentSettings settings;
    private final BufferedWriter logWriter;

    public PercentEntryPoint(PercentSettings settings) throws FinderInitializeException {
        this.settings = settings;

        // ログファイルの出力先を整備
        String logFilePath = settings.getLogFilePath();
        MyFile logFile = new MyFile(logFilePath);

        logFile.mkdirs();
        logFile.verify();

        try {
            this.logWriter = logFile.newBufferedWriter();
        } catch (IOException e) {
            throw new FinderInitializeException(e);
        }
    }

    @Override
    public void run() throws FinderException {
        output("# Setup Field");

        // Setup field
        Field field = settings.getField();
        Verify.field(field);

        // Setup max clear line
        int maxClearLine = settings.getMaxClearLine();
        Verify.maxClearLineUnder12(maxClearLine);

        // Output field
        output(FieldView.toString(field, maxClearLine));

        // Setup max depth
        int maxDepth = Verify.maxDepth(field, maxClearLine);  // パフェに必要なミノ数

        output();

        // ========================================

        // Output user-defined
        output("# Initialize / User-defined");
        output("Max clear lines: " + maxClearLine);
        output("Using hold: " + (settings.isUsingHold() ? "use" : "avoid"));
        output("Drop: " + settings.getDropType().name().toLowerCase());
        output("Searching patterns:");

        // Setup patterns
        List<String> patterns = settings.getPatterns();
        PatternGenerator generator = Verify.patterns(patterns, maxDepth);

        // Output patterns
        for (String pattern : patterns)
            output("  " + pattern);

        output();

        // ========================================

        // Setup core
        output("# Initialize / System");
        int core = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newFixedThreadPool(core);

        output("Version = " + FinderConstant.VERSION);
        output("Available processors = " + core);
        output("Necessary Pieces = " + maxDepth);

        output();

        // ========================================

        // Holdができるときは必要なミノ分（maxDepth + 1）だけを取り出す。maxDepth + 1だけないときはブロックの個数をそのまま指定
        output("# Enumerate pieces");
        int piecesDepth = generator.getDepth();
        int popCount = settings.isUsingHold() && maxDepth < piecesDepth ? maxDepth + 1 : maxDepth;
        output("Piece pop count = " + popCount);
        if (popCount < piecesDepth) {
            output();
            output("####################################################################");
            output("WARNING: Inputted pieces is more than 'necessary blocks'.");
            output("         Because reduce unnecessary pieces,");
            output("         there is a possibility of getting no expected percentages.");
            output("####################################################################");
            output();
        }

        // 探索パターンの列挙
        NormalEnumeratePieces normalEnumeratePieces = new NormalEnumeratePieces(generator, maxDepth, settings.isUsingHold());
        Set<LongPieces> searchingPieces = normalEnumeratePieces.enumerate();

        output("Searching pattern size (duplicate) = " + normalEnumeratePieces.getCounter());
        output("Searching pattern size ( no dup. ) = " + searchingPieces.size());

        output();

        // ========================================

        // 探索を行う
        output("# Search");
        output("  -> Stopwatch start");
        Stopwatch stopwatch = Stopwatch.createStartedStopwatch();

        ThreadLocal<Candidate<? extends Action>> candidateThreadLocal = createCandidateThreadLocal(settings.getDropType(), maxClearLine);
        PercentCore percentCore = new PercentCore(executorService, candidateThreadLocal, settings.isUsingHold());
        try {
            percentCore.run(field, searchingPieces, maxClearLine, maxDepth);
        } catch (ExecutionException | InterruptedException e) {
            throw new FinderExecuteException(e);
        }

        AnalyzeTree tree = percentCore.getResultTree();
        List<Pair<Pieces, Boolean>> resultPairs = percentCore.getResultPairs();

        stopwatch.stop();
        output("  -> Stopwatch stop : " + stopwatch.toMessage(TimeUnit.MILLISECONDS));

        output();

        // ========================================

        // Output tree
        output("# Output");
        output(tree.show());

        output();

        // Output failed patterns
        int treeDepth = settings.getTreeDepth();
        if (piecesDepth < treeDepth)
            treeDepth = piecesDepth;

        output(String.format("Success pattern tree [Head %d pieces]:", treeDepth));
        output(tree.tree(treeDepth));

        output("-------------------");

        int failedMaxCount = settings.getFailedCount();
        // skip if failedMaxCount == 0
        if (0 < failedMaxCount) {
            output(String.format("Fail pattern (max. %d)", failedMaxCount));

            List<Pair<Pieces, Boolean>> failedPairs = resultPairs.stream()
                    .filter(pair -> !pair.getValue())
                    .limit(failedMaxCount)
                    .collect(Collectors.toList());

            outputFailedPatterns(failedPairs);
        } else if (failedMaxCount < 0) {
            output("Fail pattern (all)");

            List<Pair<Pieces, Boolean>> failedPairs = resultPairs.stream()
                    .filter(pair -> !pair.getValue())
                    .collect(Collectors.toList());

            outputFailedPatterns(failedPairs);
        }

        output();

        // ========================================

        output("# Finalize");
        executorService.shutdown();
        output("done");
    }

    private ThreadLocal<Candidate<? extends Action>> createCandidateThreadLocal(DropType dropType, int maxClearLine) throws FinderInitializeException {
        switch (dropType) {
            case Softdrop:
//                return new LockedCandidateThreadLocal(maxClearLine);
                return new LockedNeighborCandidateThreadLocal(maxClearLine);
            case Harddrop:
                return new HarddropCandidateThreadLocal();
        }
        throw new FinderInitializeException("Unsupport droptype: droptype=" + dropType);
    }

    private void outputFailedPatterns(List<Pair<Pieces, Boolean>> failedPairs) throws FinderExecuteException {
        for (Pair<Pieces, Boolean> resultPair : failedPairs)
            output(resultPair.getKey().getPieces().toString());

        if (failedPairs.isEmpty())
            output("nothing");
    }

    private void output() throws FinderExecuteException {
        output("");
    }

    private void output(String str) throws FinderExecuteException {
        try {
            logWriter.append(str).append(LINE_SEPARATOR);
        } catch (IOException e) {
            throw new FinderExecuteException(e);
        }

        if (settings.isOutputToConsole())
            System.out.println(str);
    }

    private void flush() throws FinderExecuteException {
        try {
            logWriter.flush();
        } catch (IOException e) {
            throw new FinderExecuteException(e);
        }
    }

    @Override
    public void close() throws FinderTerminateException {
        try {
            flush();
            logWriter.close();
        } catch (IOException | FinderExecuteException e) {
            throw new FinderTerminateException(e);
        }
    }
}
