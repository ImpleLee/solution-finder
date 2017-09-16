package common.pattern;

import common.datastore.pieces.Blocks;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

class PiecesIterator implements Iterator<Blocks> {
    private final List<String> patterns;
    private PiecesStreamBuilder currentBuilder;
    private Iterator<Blocks> currentIterator;
    private int index;

    PiecesIterator(List<String> patterns) {
        this.patterns = patterns;
        this.currentBuilder = new PiecesStreamBuilder(patterns.get(index));
        this.currentIterator = currentBuilder.blocksStream().iterator();
        this.index = 1;
    }

    @Override
    public boolean hasNext() {
        return index < patterns.size() || currentIterator.hasNext();
    }

    @Override
    public Blocks next() {
        if (!hasNext())
            throw new NoSuchElementException();

        if (currentIterator.hasNext())
            return currentIterator.next();

        this.currentBuilder = new PiecesStreamBuilder(patterns.get(index));
        this.currentIterator = currentBuilder.blocksStream().iterator();
        this.index += 1;

        return currentIterator.next();
    }

    int getDepths() {
        return currentBuilder.getDepths();
    }
}
