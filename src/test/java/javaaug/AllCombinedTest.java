package javaaug;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.TryStmt;
import javaaug.Transformation;
import javaaug.TransformationTest;
import javaaug.transformations.*;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AllCombinedTest extends TransformationTest {

    @Test
    public void testSpectrum() throws Exception {
        List<Transformation.Builder> transformations = Arrays.asList(
                new SwapIfElseBranches.Builder(),
                new ReorderExpression.Builder(),
                new PermuteStatement.Builder(),
                new LowerNegation.Builder(),
                new InsertUnusedStatement.Builder(),
                new InsertLogStatement.Builder(),
                new InsertComment.Builder(),
                new ConvertSwitchToIf.Builder(),
                new ConvertLoop.Builder(),
                new ConvertAndConditionToNestedIf.Builder(),
                new SwapVariableName.Builder(),
                new WrapInTryCatch.Builder()
        );
        testSpectrum("test", "Spectrum.java", transformations, (before, after) -> {
        }, -200, 200, 0.1);
    }
}
