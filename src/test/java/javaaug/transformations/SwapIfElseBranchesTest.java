package javaaug.transformations;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.IfStmt;
import javaaug.Transformation;
import javaaug.TransformationTest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SwapIfElseBranchesTest extends TransformationTest {

    @Test
    public void test() throws Exception {
        MethodDeclaration methodDeclaration = getTestMethod("test", "SwapIfElseBranches.java");
        IfStmt ifStmt = methodDeclaration.findFirst(IfStmt.class).get();

        String before = "if(a<b){println(\"a < b\");}else{println(\"a >= b\");}";
        String after = "if(!(a<b)){println(\"a >= b\");}else{println(\"a < b\");}";

        assertStatementEquals(before, ifStmt);
        Transformation t = new SwapIfElseBranches(methodDeclaration);
        t.prepare().transform();
        IfStmt ifStmtAfter = methodDeclaration.findFirst(IfStmt.class).get();
        assertStatementEquals(after, ifStmtAfter);
    }

    @Test
    public void testSpectrum() throws Exception {
        testSpectrum("test", Arrays.asList(new SwapIfElseBranches.Builder()), (before, after) -> {
        });
    }
}
