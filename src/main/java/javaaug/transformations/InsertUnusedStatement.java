package javaaug.transformations;

import java.util.List;
import java.util.stream.Collectors;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.Statement;
import javaaug.Transformation;


public class InsertUnusedStatement extends Transformation<Transformation.StatementSite> {

    public InsertUnusedStatement(MethodDeclaration methodDeclaration) {
        super(methodDeclaration);
    }

    public static class Builder extends Transformation.Builder<InsertUnusedStatement> {
      public InsertUnusedStatement build(MethodDeclaration methodDeclaration) {
        return new InsertUnusedStatement(methodDeclaration);
      }
    }
    
    @Override
    public List<StatementSite> getSites() {
        return getMethodStatements().stream().map(StatementSite::new).collect(Collectors.toList());
    }

    public void transform(StatementSite site) {
        Statement statement = site.getStatement();
        NodeList<Statement> statements = getMethodDeclaration().getBody().get().getStatements();
        statements.addAfter(getUnusedStatement(), statement);
    }

    private Statement getUnusedStatement() {
        String unusedStr = "if (false) { int temp = 1; }";
        return StaticJavaParser.parseStatement(unusedStr);
    }
}
