package javaaug;
public class Java1 extends javaaug.TestFile {
    public void test(int a, int b) {
        int x = 100;
        int y = 200;
        int z = 300;
        float alpha = 0.3f;
        float beta = 3.134f;
        float gamma = 6.666666f;

        if(a < x) {
            printLine();
        } else {
            printLine();
        }

        if(!(a < x)) {
            printLine();
        } else {
            printLine();
        }

        if(x < a && x < b) {
            printLine();
        }

        if(a < alpha) {
            printLine();
        } else {
            printLine();
        }

        if(!(a < alpha)) {
            printLine();
        } else {
            printLine();
        }

        if(alpha < a && alpha < b) {
            printLine();
        }

        boolean isA = false;
        boolean isB = true;
        boolean isC = !isA && isB;
        boolean isD = isA && !isB;
        boolean isE = isA || !isB;

        if(isC) printLine();
        if(isD) printLine();
        if(isE) printLine();
    }
}