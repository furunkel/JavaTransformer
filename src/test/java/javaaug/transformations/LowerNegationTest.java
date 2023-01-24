package javaaug.transformations;

import com.github.javaparser.ast.body.MethodDeclaration;
import javaaug.TransformationTest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LowerNegationTest extends TransformationTest {

    @Test
    public void test() throws Exception {
        MethodDeclaration methodDeclaration = getTestMethod("test", "LowerNegation.java");
        LowerNegation t = new LowerNegation(methodDeclaration);
        assertStatementEquals("{if(!(a<b)){println(\"a>=b\");}}", methodDeclaration.getBody().get());
        t.prepare().transform();
        assertStatementEquals("{if(a>=b){println(\"a>=b\");}}", methodDeclaration.getBody().get());
    }

    @Test
    public void testSpectrum() throws Exception {
        testSpectrum("test", Arrays.asList(new LowerNegation.Builder()), (before, after) -> {
        });
    }
}
