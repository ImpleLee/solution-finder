package searcher.checker;

import common.ResultHelper;
import common.buildup.BuildUp;
import common.datastore.*;
import common.datastore.action.Action;
import common.datastore.blocks.LongPieces;
import common.datastore.blocks.Pieces;
import common.parser.BlockInterpreter;
import common.parser.OperationTransform;
import core.action.candidate.Candidate;
import core.action.candidate.LockedCandidate;
import core.action.reachable.LockedReachable;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.Piece;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.srs.MinoRotation;
import module.LongTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import searcher.common.validator.PerfectValidator;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static core.mino.Piece.*;
import static org.assertj.core.api.Assertions.assertThat;

class CheckerNoHoldTest {
    private final MinoFactory minoFactory = new MinoFactory();
    private final MinoShifter minoShifter = new MinoShifter();
    private final MinoRotation minoRotation = new MinoRotation();
    private final PerfectValidator validator = new PerfectValidator();
    private final CheckerNoHold checker = new CheckerNoHold(minoFactory, validator);

    private List<Piece> parseToBlocks(Result result) {
        return ResultHelper.createOperationStream(result)
                .map(Operation::getPiece)
                .collect(Collectors.toList());
    }

    private Operations parseToOperations(Result result) {
        return new Operations(ResultHelper.createOperationStream(result));
    }

    private void assertResult(Field field, int maxClearLine, LockedReachable reachable, List<Piece> pieces) {
        Result result = checker.getResult();

        // Check pieces is same
        List<Piece> resultPieces = parseToBlocks(result);
        assertThat(resultPieces).isEqualTo(pieces.subList(0, resultPieces.size()));

        // Check can build result
        Operations operations = parseToOperations(result);
        List<MinoOperationWithKey> operationWithKeys = OperationTransform.parseToOperationWithKeys(field, operations, minoFactory, maxClearLine);
        boolean cansBuild = BuildUp.cansBuild(field, operationWithKeys, maxClearLine, reachable);
        assertThat(cansBuild).isTrue();
    }

    @Test
    void testGraceSystem() throws Exception {
        List<Pair<List<Piece>, Boolean>> testCases = new ArrayList<Pair<List<Piece>, Boolean>>() {
            {
                add(new Pair<>(Arrays.asList(T, S, O, J), false));
                add(new Pair<>(Arrays.asList(T, O, J, S), false));
                add(new Pair<>(Arrays.asList(T, T, L, J), true));
                add(new Pair<>(Arrays.asList(T, T, S, Z), true));
                add(new Pair<>(Arrays.asList(T, S, Z, T), false));
                add(new Pair<>(Arrays.asList(J, S, Z, L), false));
                add(new Pair<>(Arrays.asList(Z, I, O, T), false));
                add(new Pair<>(Arrays.asList(I, J, J, O), true));
                add(new Pair<>(Arrays.asList(T, S, Z, J), false));
                add(new Pair<>(Arrays.asList(L, S, Z, T), false));
            }
        };

        // Field
        String marks = "" +
                "XXXXXX____" +
                "XXXXXX____" +
                "XXXXXX____" +
                "XXXXXX____" +
                "";
        Field field = FieldFactory.createField(marks);
        int maxClearLine = 4;
        int maxDepth = 4;

        // Initialize
        Candidate<Action> candidate = new LockedCandidate(minoFactory, minoShifter, minoRotation, maxClearLine);
        LockedReachable reachable = new LockedReachable(minoFactory, minoShifter, minoRotation, maxClearLine);

        // Assertion
        for (Pair<List<Piece>, Boolean> testCase : testCases) {
            // Set test case
            List<Piece> pieces = testCase.getKey();
            Boolean expectedCount = testCase.getValue();

            // Execute
            boolean isSucceed = checker.check(field, pieces, candidate, maxClearLine, maxDepth);
            assertThat(isSucceed).isEqualTo(expectedCount);

            // Check result
            if (isSucceed)
                assertResult(field, maxClearLine, reachable, pieces);
        }
    }

    @Test
    void testCase1() throws Exception {
        List<Pair<List<Piece>, Boolean>> testCases = new ArrayList<Pair<List<Piece>, Boolean>>() {
            {
                add(new Pair<>(Arrays.asList(J, I, O, L, S, Z, T), true));
                add(new Pair<>(Arrays.asList(J, O, I, L, Z, S, T), true));
                add(new Pair<>(Arrays.asList(O, J, I, L, Z, S, T), true));
            }
        };

        // Field
        String marks = "" +
                "X________X" +
                "X________X" +
                "XX______XX" +
                "XXXXXX__XX" +
                "";
        Field field = FieldFactory.createField(marks);
        int maxClearLine = 4;
        int maxDepth = 6;

        // Initialize
        Candidate<Action> candidate = new LockedCandidate(minoFactory, minoShifter, minoRotation, maxClearLine);
        LockedReachable reachable = new LockedReachable(minoFactory, minoShifter, minoRotation, maxClearLine);

        // Assertion
        for (Pair<List<Piece>, Boolean> testCase : testCases) {
            // Set test case
            List<Piece> pieces = testCase.getKey();
            Boolean expectedCount = testCase.getValue();

            // Execute
            boolean isSucceed = checker.check(field, pieces, candidate, maxClearLine, maxDepth);
            assertThat(isSucceed).isEqualTo(expectedCount);

            // Check result
            if (isSucceed)
                assertResult(field, maxClearLine, reachable, pieces);
        }
    }

    @Test
    void testCase2() throws Exception {
        List<Pair<List<Piece>, Boolean>> testCases = new ArrayList<Pair<List<Piece>, Boolean>>() {
            {
                add(new Pair<>(Arrays.asList(I, L, T, S, Z), true));
            }
        };

        // Field
        String marks = "" +
                "XX______XX" +
                "X______XXX" +
                "X______XXX" +
                "XX_XXX_XXX" +
                "";
        Field field = FieldFactory.createField(marks);
        int maxClearLine = 4;
        int maxDepth = 5;

        // Initialize
        Candidate<Action> candidate = new LockedCandidate(minoFactory, minoShifter, minoRotation, maxClearLine);
        LockedReachable reachable = new LockedReachable(minoFactory, minoShifter, minoRotation, maxClearLine);

        // Assertion
        for (Pair<List<Piece>, Boolean> testCase : testCases) {
            // Set test case
            List<Piece> pieces = testCase.getKey();
            Boolean expectedCount = testCase.getValue();

            // Execute
            boolean isSucceed = checker.check(field, pieces, candidate, maxClearLine, maxDepth);
            assertThat(isSucceed).isEqualTo(expectedCount);

            // Check result
            if (isSucceed)
                assertResult(field, maxClearLine, reachable, pieces);
        }
    }

    @Test
    void testCase3() throws Exception {
        List<Pair<List<Piece>, Boolean>> testCases = new ArrayList<Pair<List<Piece>, Boolean>>() {
            {
                add(new Pair<>(Arrays.asList(T, I, L, S, O, Z, J), false));
                add(new Pair<>(Arrays.asList(O, J, I, L, T, S, Z), false));
                add(new Pair<>(Arrays.asList(O, J, L, T, I, S, Z), true));
            }
        };

        // Field
        String marks = "" +
                "XXXXXX____" +
                "XXXXXX____" +
                "XXXXXX____" +
                "XXXXXX____" +
                "XXXXXX____" +
                "XXXXXX____" +
                "XXXXXXXX__" +
                "XXXXXXXX__" +
                "";
        Field field = FieldFactory.createField(marks);
        int maxClearLine = 8;
        int maxDepth = 7;

        // Initialize
        Candidate<Action> candidate = new LockedCandidate(minoFactory, minoShifter, minoRotation, maxClearLine);
        LockedReachable reachable = new LockedReachable(minoFactory, minoShifter, minoRotation, maxClearLine);

        // Assertion
        for (Pair<List<Piece>, Boolean> testCase : testCases) {
            // Set test case
            List<Piece> pieces = testCase.getKey();
            Boolean expectedCount = testCase.getValue();

            // Execute
            boolean isSucceed = checker.check(field, pieces, candidate, maxClearLine, maxDepth);
            assertThat(isSucceed).isEqualTo(expectedCount);

            // Check result
            if (isSucceed)
                assertResult(field, maxClearLine, reachable, pieces);
        }
    }

    @Test
    void testCaseFilledLine() throws Exception {
        List<Pair<List<Piece>, Boolean>> testCases = new ArrayList<Pair<List<Piece>, Boolean>>() {
            {
                add(new Pair<>(Arrays.asList(I, Z, L, I), true));
            }
        };

        // Field
        String marks = "" +
                "XXXXX_____" +
                "XXXXXXXXXX" +
                "XXXXXX____" +
                "XXXXXXX___" +
                "XXXXXX____" +
                "";
        Field field = FieldFactory.createField(marks);
        int maxClearLine = 5;
        int maxDepth = 4;

        // Initialize
        Candidate<Action> candidate = new LockedCandidate(minoFactory, minoShifter, minoRotation, maxClearLine);
        LockedReachable reachable = new LockedReachable(minoFactory, minoShifter, minoRotation, maxClearLine);

        // Assertion
        for (Pair<List<Piece>, Boolean> testCase : testCases) {
            // Set test case
            List<Piece> pieces = testCase.getKey();
            Boolean expectedCount = testCase.getValue();

            // Execute
            boolean isSucceed = checker.check(field, pieces, candidate, maxClearLine, maxDepth);
            assertThat(isSucceed).isEqualTo(expectedCount);

            // Check result
            if (isSucceed)
                assertResult(field, maxClearLine, reachable, pieces);
        }
    }

    @Test
    @LongTest
    void testCaseList() throws Exception {
        String resultPath = ClassLoader.getSystemResource("perfects/checker_avoidhold.txt").getPath();
        List<Pair<Pieces, Boolean>> testCases = Files.lines(Paths.get(resultPath))
                .filter(line -> !line.startsWith("//"))
                .map(line -> line.split("="))
                .map(split -> {
                    Stream<Piece> blocks = BlockInterpreter.parse(split[0]);
                    LongPieces pieces = new LongPieces(blocks);
                    boolean isSucceed = "o".equals(split[1]);
                    return new Pair<Pieces, Boolean>(pieces, isSucceed);
                })
                .collect(Collectors.toList());

        int maxDepth = 10;
        int maxClearLine = 4;
        Field field = FieldFactory.createField(maxClearLine);

        // Initialize
        Candidate<Action> candidate = new LockedCandidate(minoFactory, minoShifter, minoRotation, maxClearLine);
        LockedReachable reachable = new LockedReachable(minoFactory, minoShifter, minoRotation, maxClearLine);

        // Assertion
        for (Pair<Pieces, Boolean> testCase : testCases) {
            // Set test case
            List<Piece> pieces = testCase.getKey().getPieces();
            Boolean expectedCount = testCase.getValue();

            // Execute
            boolean isSucceed = checker.check(field, pieces, candidate, maxClearLine, maxDepth);
            assertThat(isSucceed).isEqualTo(expectedCount);

            // Check result
            if (isSucceed)
                assertResult(field, maxClearLine, reachable, pieces);
        }
    }
}
