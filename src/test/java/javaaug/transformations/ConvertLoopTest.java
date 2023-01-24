package javaaug.transformations;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.SwitchStmt;
import com.github.javaparser.ast.stmt.WhileStmt;
import javaaug.TransformationTest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConvertLoopTest extends TransformationTest {

    @Test
    public void testWhile() throws Exception {
        MethodDeclaration methodDeclaration = getTestMethod("testWhile", "ConvertLoop.java");
        assertTrue(methodDeclaration.findFirst(WhileStmt.class).isPresent());
        ConvertLoop t = new ConvertLoop(methodDeclaration);
        t.prepare().transform();
        assertFalse(methodDeclaration.findFirst(WhileStmt.class).isPresent());
        assertTrue(methodDeclaration.findFirst(ForStmt.class).isPresent());
    }

    @Test
    public void testFor() throws Exception {
        MethodDeclaration methodDeclaration = getTestMethod("testFor", "ConvertLoop.java");
        assertTrue(methodDeclaration.findFirst(ForStmt.class).isPresent());
        ConvertLoop t = new ConvertLoop(methodDeclaration);
        t.prepare().transform();
        assertFalse(methodDeclaration.findFirst(ForStmt.class).isPresent());
        assertTrue(methodDeclaration.findFirst(WhileStmt.class).isPresent());
    }

    @Test
    public void testSpectrum() throws Exception {
        testSpectrum("test", Arrays.asList(new ConvertLoop.Builder()), (before, after) -> {
        });

        testSpectrum("testWhile", "ConvertLoop.java", Arrays.asList(new ConvertLoop.Builder()), (before, after) -> {
        }, 0, 100, 1.0);

        testSpectrum("testFor", "ConvertLoop.java", Arrays.asList(new ConvertLoop.Builder()), (before, after) -> {
        }, 0, 100, 1.0);
    }
}
