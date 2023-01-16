import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.visitor.TreeVisitor;

import java.util.ArrayList;
import java.util.List;

public class ConvertAndConditionToNestedIf extends Transformation<ConvertAndConditionToNestedIf.Site> {

    public ConvertAndConditionToNestedIf(MethodDeclaration methodDeclaration) {
        super(methodDeclaration);
    }

    public static class Site {
        private final IfStmt ifStmt;
        private final BinaryExpr binaryExpr;

        public Site(IfStmt ifStmt, BinaryExpr binaryExpr) {
            this.ifStmt = ifStmt;
            this.binaryExpr = binaryExpr;
        }

        public IfStmt getIfStmt() {
            return ifStmt;
        }

        public BinaryExpr getBinaryExpr() {
            return binaryExpr;
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

                    // we cannot split if there is an else
                    // at least not without adding part of the condition to the else branch
                    if(!ifStmt.hasElseBranch()) {
                        Expression condition = ifStmt.getCondition();
                        if(condition instanceof BinaryExpr) {
                            BinaryExpr binaryExpr = (BinaryExpr) condition;
                            if(binaryExpr.getOperator() == BinaryExpr.Operator.AND) {
                                sites.add(new Site(ifStmt, binaryExpr));
                            }
                        }
                    }
                }
            }
        }.visitPreOrder(getMethodDeclaration());
        return sites;
    }

    public MethodDeclaration transform(Site site) {
        IfStmt ifStmt = site.getIfStmt();
        BinaryExpr binaryExpr = site.getBinaryExpr();

        Expression left = binaryExpr.getLeft();
        Expression right = binaryExpr.getRight();

        Statement thenStmt = ifStmt.getThenStmt();
        IfStmt nestedIf = new IfStmt();

        // TODO: allow specifying order, i.e. keep right or left as outer condition
        ifStmt.setCondition(left);
        nestedIf.setCondition(right);

        nestedIf.setThenStmt(thenStmt);
        ifStmt.setThenStmt(nestedIf);

        return getMethodDeclaration();
    }
}
