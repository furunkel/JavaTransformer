import java.util.List;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.Statement;


public class InsertUnusedStatement extends Transformation<Statement> {

    public InsertUnusedStatement(MethodDeclaration methodDeclaration) {
        super(methodDeclaration);
    }

    @Override
    public List<Statement> getSites() {
        return getMethodStatements();
    }

    public MethodDeclaration transform(Statement statement) {
        NodeList<Statement> statements = getMethodDeclaration().getBody().get().getStatements();
        statements.addAfter(getUnusedStatement(), statement);
        return getMethodDeclaration();
    }

    private Statement getUnusedStatement() {
        String unusedStr = "if (false) { int temp = 1; }";
        return StaticJavaParser.parseStatement(unusedStr);
    }
}
