package javaaug;

public class LowerNegation extends javaaug.TestFile {

    public void test(int a, int b) {
        if(!(a < b)) {
            println("a>=b");
        }
    }
}