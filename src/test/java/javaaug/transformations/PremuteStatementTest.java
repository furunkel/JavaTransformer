package javaaug.transformations;

import com.github.javaparser.ast.body.MethodDeclaration;
import javaaug.Transformation;
import javaaug.TransformationTest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PremuteStatementTest extends TransformationTest {
    @Test
    public void test() throws Exception {
        MethodDeclaration methodDeclaration = getTestMethod("test", "PermuteStatement.java");
        Transformation t = new PermuteStatement(methodDeclaration);
        t.prepare().transform();

        assertStatementEquals("{int y = 200; int x = 100; println(x + y);}", methodDeclaration.getBody().get());
    }

    @Test
    public void testSpectrum() throws Exception {
        testSpectrum("test", Arrays.asList(new PermuteStatement.Builder()), (before, after) -> {
            System.out.println(before.toString());
            System.out.println(after.toString());
        });
    }
}
