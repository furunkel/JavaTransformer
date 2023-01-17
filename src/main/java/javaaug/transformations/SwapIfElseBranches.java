package javaaug.transformations;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.UnaryExpr;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.visitor.TreeVisitor;
import javaaug.Transformation;

import java.util.ArrayList;
import java.util.List;

public class SwapIfElseBranches extends Transformation<SwapIfElseBranches.Site> {

    public SwapIfElseBranches(MethodDeclaration methodDeclaration) {
        super(methodDeclaration);
    }

    public static class Site extends Transformation.Site {
        private final IfStmt ifStmt;

        public Site(IfStmt ifStmt) {
            this.ifStmt = ifStmt;
        }

        public IfStmt getIfStmt() {
            return ifStmt;
        }
    }

    @Override
    public List<Site> getSites() {
        List<Site> sites = new ArrayList<>();

        new TreeVisitor() {
            @Override
            public void process(Node node) {
                if (node instanceof IfStmt) {
                    IfStmt ifStmt = (IfStmt) node;

                    if(ifStmt.hasElseBlock()) {
                        sites.add(new Site(ifStmt));
                    }
                }
            }
        }.visitPreOrder(getMethodDeclaration());
        return sites;
    }

    public void transform(Site site) {
        IfStmt ifStmt = site.getIfStmt();

        Statement thenStmt = ifStmt.getThenStmt();
        Statement elseStmt = ifStmt.getElseStmt().get();

        UnaryExpr negatedCondition = new UnaryExpr(new EnclosedExpr(ifStmt.getCondition()), UnaryExpr.Operator.LOGICAL_COMPLEMENT);
        System.out.println(ifStmt.getCondition());
        System.out.println(negatedCondition);

        ifStmt.setCondition(negatedCondition);
        ifStmt.setElseStmt(thenStmt);
        ifStmt.setThenStmt(elseStmt);
    }
}
