package javaaug.transformations;

import javaaug.TransformationTest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SwapVariableNameTest extends TransformationTest {
    @Test
    public void testSpectrum() throws Exception {
        testSpectrum("test", Arrays.asList(new SwapVariableName.Builder()), (before, after) -> {
            System.out.println(before.toString());
            System.out.println(after.toString());
        });
    }
}
