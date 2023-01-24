package javaaug.transformations;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.comments.LineComment;
import javaaug.TransformationTest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InsertCommentTest extends TransformationTest {

    @Test
    public void test() throws Exception {
        MethodDeclaration methodDeclaration = getTestMethod("test", "InsertComment.java");
        InsertComment t = new InsertComment(methodDeclaration, Arrays.asList("comment"));
        assertFalse(methodDeclaration.getBody().get().getStatements().stream().anyMatch(s -> s.getComment().isPresent()));
        t.prepare().transform();
        System.out.println(methodDeclaration.toString());
        assertTrue(methodDeclaration.getBody().get().getStatements().stream().anyMatch(s -> s.getComment().isPresent()));
    }

    @Test
    public void testSpectrum() throws Exception {
        testSpectrum("test", Arrays.asList(new InsertComment.Builder()), (before, after) -> {
        });
    }
}
