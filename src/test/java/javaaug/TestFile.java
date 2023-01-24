package javaaug;

import com.github.javaparser.ast.CompilationUnit;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class TestFile {

    private CompilationUnit cu;

    public void setCompilationUnit(CompilationUnit cu) {
        this.cu = cu;
    }

    public CompilationUnit getCompilationUnit() {
        return cu;
    }

    public static class Spectrum {
        List<Integer> marks = new ArrayList<>();
        HashMap<Integer, List<Object>> extras = new HashMap<>();

        public <T> void mark(int markId, T extra) {
            mark(markId);
            new ArrayList<Integer>();
            extras.compute(markId, (k, v) -> {
                if (v == null) {
                    ArrayList<Object> list = new ArrayList<>();
                    list.add(extra);
                    return list;
                } else {
                    v.add(extra);
                    return v;
                }
            });
        }

        public void mark(int markId) {
            marks.add(markId);
        }

        public boolean isEmpty() {
            return marks.isEmpty();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Spectrum spectrum = (Spectrum) o;
            return Objects.equals(marks, spectrum.marks) && Objects.equals(extras, spectrum.extras);
        }

        @Override
        public String toString() {
            return "Spectrum{" +
                    "marks=" + marks +
                    ", extras=" + extras +
                    '}';
        }

        @Override
        public int hashCode() {
            return Objects.hash(marks, extras);
        }
    }

    private Spectrum spectrum = null;

    public void mark() {
        throw new RuntimeException();
    }

    public void println(String s) {
        System.out.println(s);
    }

    public void mark(int markId) {
        spectrum.mark(markId);
    }

    public <T> void mark(int markId, T extra) {
        spectrum.mark(markId, extra);
    }

    Spectrum getSpectrum(int a, int b) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Spectrum spectrum = new Spectrum();
        this.spectrum = spectrum;
        Method method = getClass().getMethod("test", int.class, int.class);
        method.invoke(this, a, b);
        this.spectrum = null;
        return spectrum;
    }
}
