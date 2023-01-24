package javaaug;

public class ConvertLoop extends javaaug.TestFile {

    public void testWhile(int a, int b) {
        while(a < b) {
            println("loop");
            mark(a);
            a++;
        }
    }

    public void testFor(int a, int b) {
        for(int i = a; i < a + b; i++) {
            println("loop");
            mark(i);
        }
    }
}