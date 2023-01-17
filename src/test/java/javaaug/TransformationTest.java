package javaaug;

import net.openhft.compiler.CompilerUtils;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TransformationTest {
    @Test
    public void testTransformations() throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        InputStream is = TransformationTest.class.getClassLoader().getResourceAsStream("Java1.java");
        assert is != null;
        String javaCode = new String(is.readAllBytes());

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(os, true);

        System.out.println(javaCode);

        Class aClass = CompilerUtils.CACHED_COMPILER.loadFromJava("javaaug.Java1", javaCode);
        TestFile java1 = (TestFile) aClass.newInstance();
        java1.init(printStream);
        Method method = java1.getClass().getMethod("test", int.class, int.class);
        method.invoke(java1, 100, 100);
        String output = new String(os.toByteArray());
        System.out.println(output);

    }
}
