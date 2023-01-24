package javaaug.transformations;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import javaaug.TransformationTest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InsertLogStatementTest extends TransformationTest {

    @Test
    public void test() throws Exception {
        MethodDeclaration methodDeclaration = getTestMethod("test", "InsertLogStatement.java");
        InsertLogStatement t = new InsertLogStatement(methodDeclaration, Arrays.asList("log"));
        assertFalse(methodDeclaration.findFirst(MethodCallExpr.class).isPresent());
        t.prepare().transform();
        assertTrue(methodDeclaration.findFirst(MethodCallExpr.class).isPresent());
    }

    @Test
    public void testSpectrum() throws Exception {
        testSpectrum("test", Arrays.asList(new InsertLogStatement.Builder()), (before, after) -> {
        });
    }
}
