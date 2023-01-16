import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.UnaryExpr;
import com.github.javaparser.ast.visitor.TreeVisitor;

import java.util.ArrayList;
import java.util.List;

public class LowerNegation extends Transformation<LowerNegation.Site> {

    public LowerNegation(MethodDeclaration methodDeclaration) {
        super(methodDeclaration);
    }

    public static class Site {
        private final UnaryExpr unaryExpr;
        private final BinaryExpr binaryExpr;
        public UnaryExpr getUnaryExpr() {
            return unaryExpr;
        }
        public BinaryExpr getBinaryExpr() {
            return binaryExpr;
        }
        public Site(UnaryExpr unaryExpr, BinaryExpr binaryExpr) {
            this.unaryExpr = unaryExpr;
            this.binaryExpr = binaryExpr;
        }

        

    }

    @Override
    public List<Site> getSites() {
        List<Site> sites = new ArrayList<>();

        new TreeVisitor() {
            @Override
            public void process(Node node) {
                if (node instanceof UnaryExpr) {
                    UnaryExpr unaryExpr = (UnaryExpr) node;
                    if (unaryExpr.getOperator() == UnaryExpr.Operator.LOGICAL_COMPLEMENT) {
                        Expression expression = unaryExpr.getExpression();
                        if (expression.isEnclosedExpr()) {
                            expression = ((EnclosedExpr) expression).getInner();
                        }
                        if (expression.isBinaryExpr()) {
                            BinaryExpr binaryExpr = (BinaryExpr) expression;
                            if(invertibleOperator(binaryExpr.getOperator())) {
                                sites.add(new Site(unaryExpr, binaryExpr));
                            }
                        }
                    }
                }
            }
        }.visitPreOrder(getMethodDeclaration());
        return sites;
    }

    public MethodDeclaration transform(Site site) {
        UnaryExpr unaryExpr = site.getUnaryExpr();
        BinaryExpr expression = site.getBinaryExpr();

        BinaryExpr.Operator invertedOperator = invertOperator(expression.getOperator());
        boolean invertOperands = invertOperands(expression.getOperator());

        Expression invertedLeft;
        if(invertOperands) {
            invertedLeft = negateExpression(expression.getLeft());
        } else {
            invertedLeft = expression.getLeft();
        }

        Expression invertedRight;
        if(invertOperands) {
            invertedRight = negateExpression(expression.getRight());
        } else {
            invertedRight = expression.getRight();
        }

        BinaryExpr invertedExpression = new BinaryExpr(invertedLeft, invertedRight, invertedOperator);
        unaryExpr.replace(invertedExpression);
        return getMethodDeclaration();
    }

    private UnaryExpr negateExpression(Expression expression) {
        return new UnaryExpr(new EnclosedExpr(expression), UnaryExpr.Operator.LOGICAL_COMPLEMENT);
    }

    private boolean invertibleOperator(BinaryExpr.Operator operator) {
        switch(operator) {
            case AND:
            case OR:
            case LESS_EQUALS:
            case LESS:
            case GREATER_EQUALS:
            case GREATER:
            case EQUALS:
            case NOT_EQUALS:
                return true;
            default:
                return false;
        }
    }

    private boolean invertOperands(BinaryExpr.Operator operator) {
        switch(operator) {
            case AND:
            case OR:
                return true;
            default:
                return false;
        }
    }

    private BinaryExpr.Operator invertOperator(BinaryExpr.Operator operator) {
        switch(operator) {
            case AND: return BinaryExpr.Operator.OR;
            case OR: return BinaryExpr.Operator.AND;
            case LESS_EQUALS: return BinaryExpr.Operator.EQUALS;
            case LESS: return BinaryExpr.Operator.GREATER_EQUALS;
            case GREATER_EQUALS: return BinaryExpr.Operator.LESS;
            case GREATER: return BinaryExpr.Operator.LESS_EQUALS;
            case EQUALS: return BinaryExpr.Operator.NOT_EQUALS;
            case NOT_EQUALS: return BinaryExpr.Operator.EQUALS;
            default:
                throw new IllegalArgumentException();
        }
    }
}
