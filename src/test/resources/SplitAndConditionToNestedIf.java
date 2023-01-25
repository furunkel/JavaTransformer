package javaaug;

public class SplitAndConditionToTestedIf extends javaaug.TestFile {

    public void test(int a, int b) {
        if(a < b && b < 100) {
            println("ok");
        }
    }
}