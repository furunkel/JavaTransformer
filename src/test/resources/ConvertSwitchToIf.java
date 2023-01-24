package javaaug;

public class ConvertSwitchToIf extends javaaug.TestFile {

    public void test(int a, int b) {
        switch (a) {
            case 1:
            case 2:
            case 3:
            case 4:
                mark();
            case 5:
                mark();
                break;
            case 7:
            case 8:
                mark();
                break;
            case 9:
                break;
            default:
                mark();
        }
    }
}