package javaaug.transformations;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.TryStmt;
import javaaug.Transformation;
import javaaug.TransformationTest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WrapInTryCatchTest extends TransformationTest {
    @Test
    public void test() throws Exception {
        MethodDeclaration methodDeclaration = getTestMethod("test", "WrapInTryCatch.java");
        assertFalse(methodDeclaration.findFirst(TryStmt.class).isPresent());
        Transformation t = new WrapInTryCatch(methodDeclaration);
        t.prepare().transform();
        assertTrue(methodDeclaration.findFirst(TryStmt.class).isPresent());
    }

    @Test
    public void testSpectrum() throws Exception {
        testSpectrum("test", Arrays.asList(new WrapInTryCatch.Builder()), (before, after) -> {
            System.out.println(before.toString());
            System.out.println(after.toString());
        });
    }
}
