package javaaug;

public class ReorderExpression extends javaaug.TestFile {
    public void test1(int a, int b) {
        if (a < b) {
            println("a < b");
        }
    }

    public void test2(int a, int b) {
        boolean x = false;
        boolean y = true;
        if (x && y) {
            println("a && b");
        }
    }
}