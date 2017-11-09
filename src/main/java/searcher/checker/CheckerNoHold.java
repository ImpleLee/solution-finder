package searcher.checker;

import common.OperationHistory;
import common.datastore.Result;
import common.datastore.action.Action;
import common.datastore.order.DepthOrder;
import common.datastore.order.Order;
import core.action.candidate.Candidate;
import core.field.Field;
import core.mino.MinoFactory;
import core.mino.Piece;
import searcher.common.validator.Validator;
import searcher.core.SimpleSearcherCore;

import java.util.List;

public class CheckerNoHold implements Checker {
    private final CheckerDataPool dataPool;
    private final SimpleSearcherCore searcherCore;

    public CheckerNoHold(MinoFactory minoFactory, Validator validator) {
        this.dataPool = new CheckerDataPool();
        this.searcherCore = new SimpleSearcherCore(minoFactory, validator, dataPool);
    }

    @Override
    public boolean check(Field initField, List<Piece> pieces, Candidate<? extends Action> candidate, int maxClearLine, int maxDepth) {
        Piece[] blocks = new Piece[pieces.size()];
        return check(initField, pieces.toArray(blocks), candidate, maxClearLine, maxDepth);
    }

    @Override
    public boolean check(Field initField, Piece[] pieces, Candidate<? extends Action> candidate, int maxClearLine, int maxDepth) {
        Field freeze = initField.freeze(maxClearLine);
        int deleteLine = freeze.clearLine();

        dataPool.initFirst();
        dataPool.addOrder(new DepthOrder(freeze, null, maxClearLine - deleteLine, maxDepth));

        while (!dataPool.getNexts().isEmpty() && dataPool.getResults().isEmpty()) {
            Order order = dataPool.getNexts().pollLast();
            OperationHistory history = order.getHistory();
            assert history != null;
            int depth = history.getNextIndex();
            boolean isLast = depth == maxDepth;

            assert depth < pieces.length : depth;
            searcherCore.stepWithNextNoHold(candidate, pieces[depth], order, isLast);
        }

        return !dataPool.getResults().isEmpty();
    }

    public Result getResult() {
        assert !dataPool.getResults().isEmpty();
        return dataPool.getResults().get(0);
    }
}
