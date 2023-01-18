package javaaug.transformations;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.TreeVisitor;
import javaaug.Utils;
import javaaug.Transformation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PermuteStatement extends Transformation<PermuteStatement.Site> {
    public PermuteStatement(MethodDeclaration methodDeclaration) {
        super(methodDeclaration);
    }

    @Override
    public List<Site> getSites() {
        List<Site> sites = new ArrayList<>();
        List<List<Node>> basicBlockStatements = locateBasicBlockStatements(getMethodDeclaration());
        List<Node> alreadyUsed = new ArrayList<>();

        for (List<Node> basicBlockNodes : basicBlockStatements) {
            for (int i = 0; i < basicBlockNodes.size(); i++) {
                for (int j = i + 1; j < basicBlockNodes.size(); j++) {
                    Statement stmt_i = (Statement) basicBlockNodes.get(i);
                    Statement stmt_j = (Statement) basicBlockNodes.get(j);
                    if(alreadyUsed.contains(stmt_i) || alreadyUsed.contains(stmt_j)) continue;

                    if (stmt_i.getParentNode().equals(stmt_j.getParentNode())) {
                        List<SimpleName> iIdentifiers = stmt_i.findAll(SimpleName.class);
                        List<SimpleName> jIdentifiers = stmt_j.findAll(SimpleName.class);
                        List<SimpleName> ijIdentifiers = iIdentifiers.stream()
                                .filter(jIdentifiers::contains).collect(Collectors.toList());
                        if (ijIdentifiers.size() == 0) { // dependency check between i & j statement
                            List<SimpleName> bIdentifiers = new ArrayList<>();
                            for (int b = i + 1; b < j; b++) {
                                Statement stmt_b = (Statement) basicBlockNodes.get(b);
                                bIdentifiers.addAll(stmt_b.findAll(SimpleName.class));
                            }
                            List<SimpleName> ibIdentifiers = iIdentifiers.stream()
                                    .filter(bIdentifiers::contains).collect(Collectors.toList());
                            if (ibIdentifiers.size() == 0) { // dependency check among i & internal statements
                                System.out.println("AAAA");
                                List<SimpleName> jbIdentifiers = jIdentifiers.stream()
                                        .filter(bIdentifiers::contains).collect(Collectors.toList());
                                if (jbIdentifiers.size() == 0) { // dependency check among j & internal statements
                                    // swapStatementNodes(getMethodDeclaration(), basicBlockNodes, i, j, ++cnt);
                                    sites.add(new Site(stmt_i, stmt_j));
                                    alreadyUsed.add(stmt_i);
                                    alreadyUsed.add(stmt_j);
                                }
                            }
                        }
                    }
                }
            }
        }

        return sites;
    }

    public static class Site extends Transformation.Site {
        public final Statement stmtA;
        public final Statement stmtB;

        public Statement getStmtA() {
            return stmtA;
        }

        public Statement getStmtB() {
            return stmtB;
        }

        public Site(Statement stmtA, Statement stmtB) {
            this.stmtA = stmtA;
            this.stmtB = stmtB;
        }
    }

    private List<List<Node>> locateBasicBlockStatements(MethodDeclaration methodDeclaration) {
        List<Node> innerStatementNodes = new ArrayList<>();
        List<List<Node>> basicBlockNodes = new ArrayList<>();

        new TreeVisitor() {
            @Override
            public void process(Node node) {
                if (node instanceof Statement) {
                    if (node instanceof ExpressionStmt
                            && node.findAll(MethodCallExpr.class).size() == 0
                            && !Utils.isNotPermeableStatement(node)) {

                        innerStatementNodes.add(node);
                    } else {
                        if (innerStatementNodes.size() > 1) {
                            basicBlockNodes.add(new ArrayList<>(innerStatementNodes));
                        }
                        innerStatementNodes.clear();
                    }
                }
            }
        }.visitBreadthFirst(methodDeclaration);

        if (innerStatementNodes.size() > 1) {
            basicBlockNodes.add(new ArrayList<>(innerStatementNodes));
        }

        return basicBlockNodes;

    }

    public void transform(Site site) {
        Node stmtA = site.getStmtA().clone();
        Node stmtB = site.getStmtB().clone();
        site.getStmtA().replace(stmtB);
        site.getStmtB().replace(stmtA);
    }

    // private void swapStatementNodes(CompilationUnit com, List<Node>
    // basicBlockNodes, int i, int j, int cnt) {
    // CompilationUnit newCom = com.clone();
    // ArrayList<ArrayList<Node>> statementNodes =
    // locateBasicBlockStatements(newCom);
    // Statement stmt_i = (Statement) statementNodes.get(k).get(i);
    // Statement stmt_j = (Statement) statementNodes.get(k).get(j);
    // stmt_i.replace(stmt_j.clone());
    // stmt_j.replace(stmt_i.clone());
    // mCommon.saveTransformation(mSavePath, newCom, mJavaFile,
    // String.valueOf(cnt));
    // }

}
