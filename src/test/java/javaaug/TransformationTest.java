package javaaug;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import net.openhft.compiler.CompilerUtils;
import org.opentest4j.AssertionFailedError;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class TransformationTest {

    private CompilationUnit applyTransformationsInRandomOrder(CompilationUnit originalCu, String className, String methodName, List<Transformation.Builder> transformationBuilders, double transformationProbability) {
        CompilationUnit cu = originalCu.clone();
        List<MethodDeclaration> methods = Utils.findTransformableMethodDeclarations(cu);
        MethodDeclaration methodDeclaration = methods.stream().filter(m -> {
            System.out.println(m.getNameAsString());
            return m.getNameAsString().equals(methodName);
        }).findFirst().get();

        List<Transformation> transformations = transformationBuilders.stream().map(b -> b.build(methodDeclaration)).collect(Collectors.toList());
        for (int i = 0; i < 5; i++) {
//            Collections.shuffle(transformations);
            for (Transformation transformation : transformations) {
                transformation.prepare();
                transformation.transformRandom(transformationProbability);
            }
        }

//        cu.findFirst(ClassOrInterfaceDeclaration.class).ifPresent(declaration -> {
//            declaration.setName(className);
//        });

        return cu;
    }

    String replaceMarkCalls(CompilationUnit cu) {
        List<MethodCallExpr> calls = cu.findAll(MethodCallExpr.class);
        for (int i = 0; i < calls.size(); i++) {
            MethodCallExpr call = calls.get(i);
            if (call.getName().getIdentifier().equals("mark")) {
//                if (call.getArguments().size() != 0) throw new RuntimeException("found mark call with arguments");
                call.getArguments().add(0, StaticJavaParser.parseExpression("" + i));
            }
        }
        return cu.toString();
    }

    TestFile getTestFile(CompilationUnit originalCu, String className) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        CompilationUnit cu = originalCu.clone();
        cu.findFirst(ClassOrInterfaceDeclaration.class).ifPresent(declaration -> {
            declaration.setName(className);
        });
        String javaCode = cu.toString();
        Class aClass = CompilerUtils.CACHED_COMPILER.loadFromJava("javaaug." + className, javaCode);
        TestFile testFile = (TestFile) aClass.newInstance();
        testFile.setCompilationUnit(cu);
        return testFile;
    }

    @FunctionalInterface
    public interface TestTransformationBlock {
        void accept(CompilationUnit before, CompilationUnit after);
    }

    private int numPermutations(int n) {
        int s = n;
        for (int i = n - 1; i > 1; i--) {
            s *= i;
        }
        return n;
    }

    public void assertEqualSpectrum(CompilationUnit cu, String methodName, List<Transformation.Builder> transformationBuilders, TestTransformationBlock block, int minValue, int maxValue, double transformationProbability) throws Exception {
        TestFile originalTestFile = getTestFile(cu, "Java1");

        int numPermutations = Math.min(100, numPermutations(transformationBuilders.size()));
        Random random = new Random();
        for (int j = 0; j < numPermutations; j++) {
            String className = "Java" + (j + 2);
            CompilationUnit transformedCu = applyTransformationsInRandomOrder(cu, className, methodName, transformationBuilders, transformationProbability);
            if (block != null) block.accept(cu, transformedCu);
            TestFile transformedTestFile = getTestFile(transformedCu, className);
            for (int i = 0; i < 100; i++) {
                int a = random.nextInt(maxValue - minValue) + minValue;
                int b = random.nextInt(maxValue - minValue) + minValue;

                TestFile.Spectrum originalSpectrum = originalTestFile.getSpectrum(a, b);
                TestFile.Spectrum transformedSpectrum = transformedTestFile.getSpectrum(a, b);

                try {
                    assertFalse(originalSpectrum.isEmpty());
                    assertEquals(originalSpectrum, transformedSpectrum);
                } catch(AssertionFailedError e) {
                    System.out.println(transformedTestFile.getCompilationUnit().toString());
                    throw e;
                }
            }
        }
    }

    public CompilationUnit parseResource(String resourceName) throws URISyntaxException, IOException {
        URL resource = TransformationTest.class.getClassLoader().getResource(resourceName);
        File file = new File(resource.toURI());
        InputStream is = resource.openStream();
        assert is != null;
        assert file != null;

        String javaCode = new String(is.readAllBytes());
        CompilationUnit cu = Utils.parse(file, javaCode);
        replaceMarkCalls(cu);

        return cu;
    }

    public MethodDeclaration getTestMethod(String methodName, String resourceName) throws URISyntaxException, IOException {
        CompilationUnit cu = parseResource(resourceName);
        MethodDeclaration methodDeclaration = cu.findFirst(MethodDeclaration.class, m -> m.getNameAsString().equals(methodName)).get();
        return methodDeclaration;
    }

    public void assertStatementEquals(String expected, Node node) {
        Node expectedNode = StaticJavaParser.parseStatement(expected);
        assertEquals(expectedNode, node);
    }

    public void assertExpressionEquals(String expected, Node node) {
        Node expectedNode = StaticJavaParser.parseExpression(expected);
        assertEquals(expectedNode, node);
    }

    public void testSpectrum(String methodName, String resourceName, List<Transformation.Builder> transformationBuilders, TestTransformationBlock block, int minValue, int maxValue, double transformationProbability) throws Exception {
        CompilationUnit originalCu = parseResource(resourceName);
        assertEqualSpectrum(originalCu, methodName, transformationBuilders, block, minValue, maxValue, transformationProbability);
//        TestFile originalTestFile = getTestFile(originalCu, "Java1");
//        System.out.println(javaCode);
//
//
//
//        assertEqualSpectrum();
//        Random random = new Random();
//        for(int j = 0; j < 100; j++) {
//            String className = "Java" + (j + 1);
//
//            CompilationUnit transformedCu = applyTransformationsInRandomOrder(originalCu, className, methodName, transformationBuilders);
//            if(block != null) block.accept(originalCu, transformedCu);
//            TestFile transformedTestFile = getTestFile(transformedCu, className);
//            for (int i = 0; i < 100; i++) {
//                int a = random.nextInt(2000) - 1000;
//                int b = random.nextInt(2000) - 1000;
//
//                List<Integer> originalSpectrum = originalTestFile.getSpectrum(a, b);
//                List<Integer> transformedSpectrum = transformedTestFile.getSpectrum(a, b);
//
//                assertEquals(originalSpectrum, transformedSpectrum);
//            }
//        }
    }

    public void testSpectrum(String methodName, List<Transformation.Builder> transformationBuilders, TestTransformationBlock block) throws Exception {
        testSpectrum(methodName, "Spectrum.java", transformationBuilders, block, 1, 10, 1.0);
    }


//    @Test
//    public void testNegateBoolean() throws Exception {
//        testTransformations("test", Arrays.asList(new NegateBoolean.Builder()), (before, after) -> {
//            System.out.println(before.toString());
//            System.out.println(after.toString());
//        });
//    }

}
