package entry.path;

import common.buildup.BuildUpStream;
import common.datastore.OperationWithKey;
import common.datastore.Pair;
import common.datastore.pieces.Blocks;
import common.datastore.pieces.LongBlocks;
import common.order.OrderLookup;
import common.order.StackOrder;
import common.pattern.BlocksGenerator;
import core.field.Field;
import core.mino.Block;
import core.mino.Mino;
import searcher.pack.SizedBit;
import searcher.pack.task.PackSearcher;
import searcher.pack.task.Result;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;

class PathCore {
    private final List<Result> candidates;
    private final HashSet<LongBlocks> validPieces;

    private List<Pair<Result, HashSet<LongBlocks>>> unique = null;
    private LinkedList<Pair<Result, HashSet<LongBlocks>>> minimal = null;

    PathCore(List<String> patterns, PackSearcher searcher, int maxDepth, boolean isUsingHold) throws ExecutionException, InterruptedException {
        this.candidates = searcher.collect(Collectors.toList());

        // ホールド込みで有効な手順を列挙する
        this.validPieces = getCollect(patterns, maxDepth, isUsingHold);
    }

    private HashSet<LongBlocks> getCollect(List<String> patterns, int maxDepth, boolean isUsingHold) {
        BlocksGenerator blocksGenerator = new BlocksGenerator(patterns);

        // 必要以上にミノを使っている場合はリストを削減する
        Function<List<Block>, List<Block>> reduceBlocks = Function.identity();
        if (maxDepth < blocksGenerator.getDepth())
            reduceBlocks = blocks -> blocks.subList(0, maxDepth);

        if (isUsingHold) {
            return blocksGenerator.stream()
                    .parallel()
                    .map(Blocks::getBlockList)
                    .flatMap(blocks -> OrderLookup.forwardBlocks(blocks, maxDepth).stream())
                    .collect(Collectors.toCollection(HashSet::new))
                    .parallelStream()
                    .map(StackOrder::toList)
                    .map(reduceBlocks)
                    .map(LongBlocks::new)
                    .collect(Collectors.toCollection(HashSet::new));
        } else {
            return blocksGenerator.stream()
                    .parallel()
                    .map(Blocks::getBlockList)
                    .map(reduceBlocks)
                    .map(LongBlocks::new)
                    .collect(Collectors.toCollection(HashSet::new));
        }
    }

    void runUnique(Field field, SizedBit sizedBit) {
        // uniqueの作成
        LockedBuildUpListUpThreadLocal threadLocal = new LockedBuildUpListUpThreadLocal(sizedBit.getHeight());
        this.unique = candidates.parallelStream()
                .map(result -> {
                    LinkedList<OperationWithKey> operations = result.getMemento().getOperationsStream(sizedBit.getWidth()).collect(Collectors.toCollection(LinkedList::new));

                    BuildUpStream buildUpStream = threadLocal.get();
                    HashSet<LongBlocks> pieces = buildUpStream.existsValidBuildPatternDirectly(field, operations)
                            .map(operationWithKeys -> operationWithKeys.stream()
                                    .map(OperationWithKey::getMino)
                                    .map(Mino::getBlock)
                                    .collect(Collectors.toList())
                            )
                            .map(LongBlocks::new)
                            .filter(validPieces::contains)
                            .collect(Collectors.toCollection(HashSet::new));
                    return new Pair<>(result, pieces);
                })
                .filter(pair -> !pair.getValue().isEmpty())
                .collect(Collectors.toList());
    }

    void runMinimal() {
        // 他のパターンではカバーできないものだけを列挙する
        LinkedList<Pair<Result, HashSet<LongBlocks>>> minimal = new LinkedList<>();
        for (Pair<Result, HashSet<LongBlocks>> pair : unique) {
            HashSet<LongBlocks> canBuildBlocks = pair.getValue();
            boolean isSetNeed = true;
            LinkedList<Pair<Result, HashSet<LongBlocks>>> nextMasters = new LinkedList<>();

            // すでに登録済みのパターンでカバーできるか確認
            while (!minimal.isEmpty()) {
                Pair<Result, HashSet<LongBlocks>> targetPair = minimal.pollFirst();
                Set<LongBlocks> registeredBlocks = targetPair.getValue();

                if (registeredBlocks.size() < canBuildBlocks.size()) {
                    // 新しいパターンの方が多く対応できる  // 新パターンが残る
                    HashSet<LongBlocks> newTarget = new HashSet<>(registeredBlocks);
                    newTarget.removeAll(canBuildBlocks);

                    // 新パターンでも対応できないパターンがあるときは残す
                    if (newTarget.size() != 0)
                        nextMasters.add(targetPair);
                } else if (canBuildBlocks.size() < registeredBlocks.size()) {
                    // 登録済みパターンの方が多く対応できる
                    HashSet<LongBlocks> newSet = new HashSet<>(canBuildBlocks);
                    newSet.removeAll(registeredBlocks);

                    // 登録済みパターンを残す
                    nextMasters.add(targetPair);

                    if (newSet.size() == 0) {
                        // 上位のパターンが存在するので新パターンはいらない
                        // 残りの登録済みパターンは無条件で残す
                        isSetNeed = false;
                        nextMasters.addAll(minimal);
                        break;
                    }
                } else {
                    // 新パターンと登録済みパターンが対応できる数は同じ
                    HashSet<LongBlocks> newSet = new HashSet<>(canBuildBlocks);
                    newSet.retainAll(registeredBlocks);

                    // 登録済みパターンを残す
                    nextMasters.add(targetPair);

                    if (newSet.size() == registeredBlocks.size()) {
                        // 完全に同一の対応パターンなので新パターンはいらない
                        // 残りの登録済みパターンは無条件で残す
                        isSetNeed = false;
                        nextMasters.addAll(minimal);
                        break;
                    }
                }
            }

            // 新パターンが必要
            if (isSetNeed)
                nextMasters.add(pair);

            minimal = nextMasters;
        }

        this.minimal = minimal;
    }

    List<Pair<Result, HashSet<LongBlocks>>> getUnique() {
        return unique;
    }

    List<Pair<Result, HashSet<LongBlocks>>> getMinimal() {
        return minimal;
    }
}
