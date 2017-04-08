package searcher.common.order;

import core.field.Field;
import core.mino.Block;
import searcher.common.History;

public class Order implements Comparable<Order> {
    private final Block hold;
    private final Field field;
    private final int maxClearLine;
    private final History history;

    public Order(Field field, Block hold, int maxClearLine, int maxDepth) {
        this(field, hold, maxClearLine, new History(maxDepth - 1));
    }

    public Order(Field field, Block hold, int maxClearLine, History history) {
        this.field = field;
        this.hold = hold;
        this.maxClearLine = maxClearLine;
        this.history = history;
    }

    public History getHistory() {
        return history;
    }

    public Block getHold() {
        return hold;
    }

    public Field getField() {
        return field;
    }

    public int getMaxClearLine() {
        return maxClearLine;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Order order = (Order) o;

        return this.compareTo(order) == 0;
    }

    @Override
    public int hashCode() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int compareTo(Order o) {
        int compare = compare(o);
        if (compare == 0)
            return compare;
        return 0 < compare ? 1 : -1;
    }

    private int compare(Order o) {
        if (hold == o.hold) {
            return compareFieldTo(field, o.field);
        } else {
            int number = hold != null ? hold.getNumber() : 7;
            int number1 = o.hold != null ? o.hold.getNumber() : 7;
            return sign(number, number1);
        }
    }

    private int compareFieldTo(Field left, Field right) {
        // 高さを比較する
        int leftMaxIndex = left.getBoardCount();
        int height = leftMaxIndex - right.getBoardCount();
        if (height != 0)
            return sign(height);

        // フィールドの中身を比較する
        for (int index = 0; index < leftMaxIndex; index++) {
            long l = left.getBoard(index) - right.getBoard(index);
            if (l != 0L)
                return sign(l);
        }
        return 0;
    }

    private int sign(int value) {
        return 0 < value ? 1 : -1;
    }

    private int sign(long value) {
        return 0 < value ? 1 : -1;
    }

    private int sign(int left, int right) {
        return left > right ? 1 : -1;
    }
}
