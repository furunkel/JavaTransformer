package javaaug;

public class WrapInTryCatch extends javaaug.TestFile {
    public void test(int a, int b) {
        double x = (double) a * 1.32394839;
        if (x > (double) b) {
            System.out.println("yes");
            mark();
        } else {
            System.out.println("no");
            mark();
        }
        if (x - 100.0 > 0) {
            mark();
        } else {
            mark();
        }
    }
}