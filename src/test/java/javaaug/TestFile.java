package javaaug;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class TestFile {
    List<Integer> marks;

    public void mark() {
        throw new RuntimeException();
    }

    public void mark(int markId) {
        marks.add(markId);
    }

    List<Integer> getSpectrum(int a, int b) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        List<Integer> marks = new ArrayList<Integer>();
        this.marks = marks;
        Method method = getClass().getMethod("test", int.class, int.class);
        method.invoke(this, a, b);
        this.marks = null;
        return marks;
    }
}
