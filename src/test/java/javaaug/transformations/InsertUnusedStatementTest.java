package javaaug.transformations;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.IfStmt;
import javaaug.TransformationTest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InsertUnusedStatementTest extends TransformationTest {

    @Test
    public void test() throws Exception {
        MethodDeclaration methodDeclaration = getTestMethod("test", "InsertUnusedStatement.java");
        InsertUnusedStatement t = new InsertUnusedStatement(methodDeclaration);
        assertFalse(methodDeclaration.findFirst(IfStmt.class).isPresent());
        t.prepare().transform();
        assertTrue(methodDeclaration.findFirst(IfStmt.class).isPresent());
    }

    @Test
    public void testSpectrum() throws Exception {
        testSpectrum("test", Arrays.asList(new InsertUnusedStatement.Builder()), (before, after) -> {
        });
    }
}
