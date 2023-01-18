package javaaug;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import javaaug.transformations.LowerNegation;
import net.openhft.compiler.CompilerUtils;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TransformationTest {

    private String randomTransformation(File javaFile, String javaCode, String className) throws IOException {
        CompilationUnit cu = Utils.parse(javaFile, javaCode);
        return randomTransformation(cu, className);
    }

    private String randomTransformation(CompilationUnit cu, String className) {
        List<MethodDeclaration> methods = Utils.findTransformableMethodDeclarations(cu);
        for (MethodDeclaration methodDeclaration : methods) {
            List<Transformation> transformations = Arrays.asList(
                    new LowerNegation(methodDeclaration).prepare()
            );
            for (int i = 0; i < 5; i++) {
                Collections.shuffle(transformations);
                for (Transformation transformation : transformations) {
                    transformation.transformRandom(0.2);
                }
            }
        }
        cu.findFirst(ClassOrInterfaceDeclaration.class).ifPresent(declaration -> {
            declaration.setName(className);
        });

        return cu.toString();
    }

    String replaceMarkCalls(String javaCode) {
        CompilationUnit cu = StaticJavaParser.parse(javaCode);
        List<MethodCallExpr> calls = cu.findAll(MethodCallExpr.class);
        for(int i = 0; i < calls.size(); i++) {
            MethodCallExpr call = calls.get(i);
            if (call.getName().getIdentifier().equals("mark")) {
                if (call.getArguments().size() != 0) throw new RuntimeException("found mark call with arguments");
                call.addArgument("" + i);
            }
        }
        return cu.toString();
    }

    TestFile getTestFile(String javaCode, String className) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        Class aClass = CompilerUtils.CACHED_COMPILER.loadFromJava("javaaug." + className, javaCode);
        TestFile testFile = (TestFile) aClass.newInstance();
        return testFile;
    }

    @Test
    public void testTransformations() throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, URISyntaxException {
        URL resource = TransformationTest.class.getClassLoader().getResource("Java1.java");

        File file = new File(resource.toURI());
        InputStream is = resource.openStream();
        assert is != null;
        assert file != null;

        String javaCode = new String(is.readAllBytes());
        javaCode = replaceMarkCalls(javaCode);
        System.out.println(javaCode);

        TestFile originalTestFile = getTestFile(javaCode, "Java1");

        System.out.println(javaCode);

        Random random = new Random();
        for(int j = 0; j < 100; j++) {
            String className = "Java" + (j + 1);
            String transformedCode = randomTransformation(file, javaCode, className);
            TestFile transformedTestFile = getTestFile(transformedCode, className);
            for (int i = 0; i < 100; i++) {
                int a = random.nextInt(2000) - 1000;
                int b = random.nextInt(2000) - 1000;

                List<Integer> originalSpectrum = originalTestFile.getSpectrum(a, b);
                List<Integer> transformedSpectrum = transformedTestFile.getSpectrum(a, b);

                assertEquals(originalSpectrum, transformedSpectrum);
            }
        }
    }
}
