package entry;

import exceptions.FinderException;
import exceptions.FinderTerminateException;
import exceptions.FinderExecuteException;

public interface EntryPoint {
    int run() throws FinderException;

    void close() throws FinderTerminateException;
}
