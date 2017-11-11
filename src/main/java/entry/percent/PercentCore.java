package entry.percent;

import common.datastore.Pair;
import common.datastore.action.Action;
import common.datastore.blocks.LongPieces;
import common.datastore.blocks.Pieces;
import common.tree.AnalyzeTree;
import concurrent.checker.CheckerNoHoldThreadLocal;
import concurrent.checker.CheckerUsingHoldThreadLocal;
import concurrent.checker.invoker.CheckerCommonObj;
import concurrent.checker.invoker.ConcurrentCheckerInvoker;
import concurrent.checker.invoker.no_hold.ConcurrentCheckerNoHoldInvoker;
import concurrent.checker.invoker.using_hold.ConcurrentCheckerUsingHoldInvoker;
import core.action.candidate.Candidate;
import core.action.reachable.Reachable;
import core.field.Field;
import core.mino.MinoFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

class PercentCore {
    private final ConcurrentCheckerInvoker invoker;

    private AnalyzeTree resultTree;
    private List<Pair<Pieces, Boolean>> resultPairs;

    PercentCore(ExecutorService executorService, ThreadLocal<Candidate<Action>> candidateThreadLocal, boolean isUsingHold, ThreadLocal<? extends Reachable> reachableThreadLocal, MinoFactory minoFactory, int fromDepth) {
        this.invoker = createConcurrentCheckerInvoker(executorService, candidateThreadLocal, isUsingHold, reachableThreadLocal, minoFactory, fromDepth);
    }

    private ConcurrentCheckerInvoker createConcurrentCheckerInvoker(ExecutorService executorService, ThreadLocal<Candidate<Action>> candidateThreadLocal, boolean isUsingHold, ThreadLocal<? extends Reachable> reachableThreadLocal, MinoFactory minoFactory, int fromDepth) {
        if (isUsingHold) {
            CheckerUsingHoldThreadLocal<Action> checkerThreadLocal = new CheckerUsingHoldThreadLocal<>();
            CheckerCommonObj commonObj = new CheckerCommonObj(minoFactory, candidateThreadLocal, checkerThreadLocal, reachableThreadLocal);
            return new ConcurrentCheckerUsingHoldInvoker(executorService, commonObj, fromDepth);
        } else {
            CheckerNoHoldThreadLocal<Action> checkerThreadLocal = new CheckerNoHoldThreadLocal<>();
            CheckerCommonObj commonObj = new CheckerCommonObj(minoFactory, candidateThreadLocal, checkerThreadLocal, reachableThreadLocal);
            return new ConcurrentCheckerNoHoldInvoker(executorService, commonObj);
        }
    }

    void run(Field field, Set<LongPieces> searchingPiecesSet, int maxClearLine, int maxDepth) throws ExecutionException, InterruptedException {
        List<Pieces> searchingPieces = new ArrayList<>(searchingPiecesSet);

        this.resultPairs = invoker.search(field, searchingPieces, maxClearLine, maxDepth);

        // 最低限の探索結果を集計する
        this.resultTree = new AnalyzeTree();
        for (Pair<Pieces, Boolean> resultPair : resultPairs) {
            Pieces pieces = resultPair.getKey();
            Boolean result = resultPair.getValue();
            resultTree.set(result, pieces);
        }
    }

    AnalyzeTree getResultTree() {
        return resultTree;
    }

    List<Pair<Pieces, Boolean>> getResultPairs() {
        return resultPairs;
    }
}
