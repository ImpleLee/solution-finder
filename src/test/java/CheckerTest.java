import action.candidate.Candidate;
import action.candidate.LockedCandidate;
import analyzer.CheckerTree;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.Block;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.srs.MinoRotation;
import misc.iterable.CombinationIterable;
import misc.iterable.PermutationIterable;
import org.junit.Test;
import searcher.checker.Checker;
import searcher.common.action.Action;
import searcher.common.validator.PerfectValidator;

import java.util.Arrays;
import java.util.List;

import static core.mino.Block.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CheckerTest {
    @Test
    public void testGraceSystem() throws Exception {
        // Invoker
        List<Block> blocks = Arrays.asList(I, T, S, Z, J, L, O);
        int popCount = 4;
        int maxClearLine = 4;
        int maxDepth = 4;

        // Field
        String marks = "" +
                "XXXXXX____" +
                "XXXXXX____" +
                "XXXXXX____" +
                "XXXXXX____" +
                "";
        Field field = FieldFactory.createField(marks);

        // Initialize
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = new MinoRotation();
        PerfectValidator validator = new PerfectValidator();
        Checker<Action> checker = new Checker<>(minoFactory, validator);

        // Measure
        Candidate<Action> candidate = new LockedCandidate(minoFactory, minoShifter, minoRotation, maxClearLine);
        CheckerTree tree = new CheckerTree();
        Iterable<List<Block>> permutations = new CombinationIterable<>(blocks, popCount);
        for (List<Block> permutation : permutations) {
            Iterable<List<Block>> combinations = new PermutationIterable<>(permutation);
            for (List<Block> combination : combinations) {
                combination.add(0, Block.T);  // Hold分の追加
                boolean result = checker.check(field, combination, candidate, maxClearLine, maxDepth);
                tree.set(result, combination);
            }
        }

        assertThat(tree.getSuccessPercent(), is(744 / 840.0));
    }

    @Test
    public void testTemplate() throws Exception {
        // Invoker
        List<Block> blocks = Arrays.asList(I, T, S, Z, J, L, O);
        int popCount = 4;
        int maxClearLine = 4;
        int maxDepth = 3;

        // Field
        String marks = "" +
                "XXXXX____X" +
                "XXXXXX___X" +
                "XXXXXXX__X" +
                "XXXXXX___X" +
                "";
        Field field = FieldFactory.createField(marks);

        // Initialize
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = new MinoRotation();
        PerfectValidator validator = new PerfectValidator();
        Checker<Action> checker = new Checker<>(minoFactory, validator);

        // Measure
        Candidate<Action> candidate = new LockedCandidate(minoFactory, minoShifter, minoRotation, maxClearLine);
        CheckerTree tree = new CheckerTree();
        Iterable<List<Block>> permutations = new CombinationIterable<>(blocks, popCount);
        for (List<Block> permutation : permutations) {
            Iterable<List<Block>> combinations = new PermutationIterable<>(permutation);
            for (List<Block> combination : combinations) {
                boolean result = checker.check(field, combination, candidate, maxClearLine, maxDepth);
                tree.set(result, combination);
            }
        }

        assertThat(tree.getSuccessPercent(), is(514 / 840.0));
    }

    @Test
    public void testTemplateWithHoldI() throws Exception {
        // Invoker
        List<Block> blocks = Arrays.asList(I, T, S, Z, J, L, O);
        int popCount = 4;
        int maxClearLine = 4;
        int maxDepth = 4;

        // Field
        String marks = "" +
                "XXXXX_____" +
                "XXXXXX____" +
                "XXXXXXX___" +
                "XXXXXX____" +
                "";
        Field field = FieldFactory.createField(marks);

        // Initialize
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = new MinoRotation();
        PerfectValidator validator = new PerfectValidator();
        Checker<Action> checker = new Checker<>(minoFactory, validator);

        // Measure
        Candidate<Action> candidate = new LockedCandidate(minoFactory, minoShifter, minoRotation, maxClearLine);
        CheckerTree tree = new CheckerTree();
        Iterable<List<Block>> permutations = new CombinationIterable<>(blocks, popCount);
        for (List<Block> permutation : permutations) {
            Iterable<List<Block>> combinations = new PermutationIterable<>(permutation);
            for (List<Block> combination : combinations) {
                combination.add(0, I);  // Hold分の追加
                boolean result = checker.check(field, combination, candidate, maxClearLine, maxDepth);
                tree.set(result, combination);
            }
        }

        assertThat(tree.getSuccessPercent(), is(711 / 840.0));
    }

    @Test
    public void testAfter4Line() throws Exception {
        // Invoker
        List<Block> blocks = Arrays.asList(I, T, S, Z, J, L, O);
        int popCount = 7;
        int maxClearLine = 4;
        int maxDepth = 6;

        // Field
        String marks = "" +
                "XXXX______" +
                "XXXX______" +
                "XXXX______" +
                "XXXX______" +
                "";
        Field field = FieldFactory.createField(marks);

        // Initialize
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = new MinoRotation();
        PerfectValidator validator = new PerfectValidator();
        Checker<Action> checker = new Checker<>(minoFactory, validator);

        // Measure
        Candidate<Action> candidate = new LockedCandidate(minoFactory, minoShifter, minoRotation, maxClearLine);
        CheckerTree tree = new CheckerTree();
        Iterable<List<Block>> combinations = new PermutationIterable<>(blocks);
        for (List<Block> combination : combinations) {
            boolean result = checker.check(field, combination, candidate, maxClearLine, maxDepth);
            tree.set(result, combination);
        }

        assertThat(tree.getSuccessPercent(), is(5040 / 5040.0));
    }
}
