package javaaug.transformations;

import com.github.javaparser.ast.body.MethodDeclaration;
import javaaug.TransformationTest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class ConvertAndConditionToNestedIfTest extends TransformationTest {

    @Test
    public void test() throws Exception {
        MethodDeclaration methodDeclaration = getTestMethod("test", "ConvertAndConditionToNestedIf.java");
        ConvertAndConditionToNestedIf t = new ConvertAndConditionToNestedIf(methodDeclaration);
        assertStatementEquals("{if(a<b&&b<100){println(\"ok\");}}", methodDeclaration.getBody().get());
        t.prepare().transform();
        assertStatementEquals("{if(a<b)if(b<100){println(\"ok\");}}", methodDeclaration.getBody().get());
    }

    @Test
    public void testSpectrum() throws Exception {
        testSpectrum("test", Arrays.asList(new ConvertAndConditionToNestedIf.Builder()), (before, after) -> {
        });
    }
}
