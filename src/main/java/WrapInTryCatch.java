import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class WrapInTryCatch extends Transformation<Statement> {

    public WrapInTryCatch(MethodDeclaration methodDeclaration) {
        super(methodDeclaration);
    }

    @Override
    public List<Statement> getSites() {
        MethodDeclaration methodDeclaration = getMethodDeclaration();
        if (methodDeclaration.findAll(TryStmt.class).size() > 0
                || methodDeclaration.findAll(MethodCallExpr.class).size() == 0) {
            return Collections.emptyList();
        }

        List<Statement> tcStmts = new ArrayList<>();

        if (methodDeclaration.getBody().isPresent()) {
            for (Statement statement : methodDeclaration.getBody().get().getStatements()) {
                boolean flag = true;
                if (Common.isNotPermeableStatement(statement)
                        || statement.findAll(MethodCallExpr.class).size() == 0) {
                    flag = false;
                } else if (statement instanceof ExpressionStmt) {
                    for (Node node : statement.getChildNodes()) {
                        if (node.findFirst(VariableDeclarator.class).isPresent()) {
                            flag = false;
                            break;
                        }
                    }
                }
                if (flag) {
                    tcStmts.add(statement);
                }
            }
        }
        return tcStmts;
    }

    public MethodDeclaration transform(Statement tcStmt) {
        tcStmt.replace(getTryCatchStatement(tcStmt));
        return getMethodDeclaration();
    }

    private Statement getTryCatchStatement(Statement stmt) {
        String tryStr = "try {\n" +
                stmt + "\n" +
                "} catch (Exception ex) {\n" +
                "ex.printStackTrace();\n" +
                "}";
        return StaticJavaParser.parseStatement(tryStr);
    }
}
