package javaaug.transformations;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.SwitchStmt;
import javaaug.TransformationTest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConvertSwitchToIfTest extends TransformationTest {

    @Test
    public void test() throws Exception {
        MethodDeclaration methodDeclaration = getTestMethod("test", "ConvertSwitchToIf.java");
        assertTrue(methodDeclaration.findFirst(SwitchStmt.class).isPresent());
        ConvertSwitchToIf t = new ConvertSwitchToIf(methodDeclaration);
        t.prepare().transform();
        assertFalse(methodDeclaration.findFirst(SwitchStmt.class).isPresent());
    }

    @Test
    public void testSpectrum() throws Exception {
        testSpectrum("test", Arrays.asList(new ConvertSwitchToIf.Builder()), (before, after) -> {
        });
    }
}
