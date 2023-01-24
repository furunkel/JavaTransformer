package javaaug;

public class SwapIfElseBranches extends javaaug.TestFile {
    public void test(int a, int b) {
        if (a < b) {
            println("a < b");
        } else {
            println("a >= b");
        }
    }
}