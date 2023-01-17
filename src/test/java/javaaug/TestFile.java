package javaaug;

import java.io.PrintStream;

public class TestFile {
    PrintStream p;

    public void init(PrintStream p) {
        this.p = p;
    }

    public void printLine() {
        int lineNumber = Thread.currentThread().getStackTrace()[2].getLineNumber();
        p.println(lineNumber);
    }
}
