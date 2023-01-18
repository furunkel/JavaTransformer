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
            mark();
        } else {
            mark();
        }

        if(!(a < x)) {
            mark();
        } else {
            mark();
        }

        if(x < a && x < b) {
            mark();
        }

        if(a < alpha) {
            mark();
        } else {
            mark();
        }

        if(!(a < alpha)) {
            mark();
        } else {
            mark();
        }

        if(alpha < a && alpha < b) {
            mark();
        }

        boolean isA = false;
        boolean isB = true;
        boolean isC = !isA && isB;
        boolean isD = isA && !isB;
        boolean isE = isA || !isB;

        if(isC) mark();
        if(isD) mark();
        if(isE) mark();
    }
}