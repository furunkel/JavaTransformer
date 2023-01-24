package javaaug.transformations;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.IfStmt;
import javaaug.Transformation;
import javaaug.TransformationTest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReorderExpressionTest extends TransformationTest {

    @Test
    public void testAsymetricExpression() throws Exception {
        MethodDeclaration methodDeclaration = getTestMethod("test1", "ReorderExpression.java");
        Expression condition = methodDeclaration.findFirst(IfStmt.class).get().getCondition();

        String before = "a < b";
        String after = "b > a";

        assertExpressionEquals(before, condition);
        Transformation t = new ReorderExpression(methodDeclaration);
        t.prepare().transform();
        Expression conditionAfter = methodDeclaration.findFirst(IfStmt.class).get().getCondition();
        assertExpressionEquals(after, conditionAfter);
    }

    @Test
    public void testSymmetricExpression() throws Exception {
        MethodDeclaration methodDeclaration = getTestMethod("test2", "ReorderExpression.java");
        Expression condition = methodDeclaration.findFirst(IfStmt.class).get().getCondition();

        String before = "x && y";
        String after = "y && x";

        assertExpressionEquals(before, condition);
        Transformation t = new ReorderExpression(methodDeclaration);
        t.prepare().transform();
        Expression conditionAfter = methodDeclaration.findFirst(IfStmt.class).get().getCondition();
        assertExpressionEquals(after, conditionAfter);
    }

    @Test
    public void testSpectrum() throws Exception {
        testSpectrum("test", Arrays.asList(new ReorderExpression.Builder()), (before, after) -> {
        });
    }
}
