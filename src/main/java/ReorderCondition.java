import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.visitor.TreeVisitor;

import java.util.ArrayList;
import java.util.List;

public class ReorderCondition extends Transformation<Node> {

    public ReorderCondition(MethodDeclaration methodDeclaration) {
        super(methodDeclaration);
    }

    @Override
    public List<Node> getSites() {
        return locateOperators(getMethodDeclaration());
    }

    private List<Node> locateOperators(MethodDeclaration methodDeclaration) {
        List<Node> operatorNodes = new ArrayList<>();
        new TreeVisitor() {
            @Override
            public void process(Node node) {
                if (node instanceof BinaryExpr && isAugmentationApplicable(((BinaryExpr) node).getOperator())) {
                    operatorNodes.add(node);
                }
            }
        }.visitPreOrder(methodDeclaration);
        return operatorNodes;
    }

    public MethodDeclaration transform(Node opNode) {
        new TreeVisitor() {
            @Override
            public void process(Node node) {
                if (node.equals(opNode)) {
                    BinaryExpr replNode = (BinaryExpr) opNode.clone();
                    switch (((BinaryExpr) node).getOperator()) {
                        case LESS:
                            replNode.setOperator(BinaryExpr.Operator.GREATER);
                            replNode.setLeft(((BinaryExpr) node).getRight());
                            replNode.setRight(((BinaryExpr) node).getLeft());
                            break;
                        case LESS_EQUALS:
                            replNode.setOperator(BinaryExpr.Operator.GREATER_EQUALS);
                            replNode.setLeft(((BinaryExpr) node).getRight());
                            replNode.setRight(((BinaryExpr) node).getLeft());
                            break;
                        case GREATER:
                            replNode.setOperator(BinaryExpr.Operator.LESS);
                            replNode.setLeft(((BinaryExpr) node).getRight());
                            replNode.setRight(((BinaryExpr) node).getLeft());
                            break;
                        case GREATER_EQUALS:
                            replNode.setOperator(BinaryExpr.Operator.LESS_EQUALS);
                            replNode.setLeft(((BinaryExpr) node).getRight());
                            replNode.setRight(((BinaryExpr) node).getLeft());
                            break;
                        case EQUALS:
                        case NOT_EQUALS:
                        case OR:
                        case AND:
                        case PLUS:
                        case MULTIPLY:
                            replNode.setLeft(((BinaryExpr) node).getRight());
                            replNode.setRight(((BinaryExpr) node).getLeft());
                            break;
                        default:
                            throw new RuntimeException("unexpected operator");
                    }
                    node.replace(replNode);
                }
            }
        }.visitPreOrder(getMethodDeclaration());
        return getMethodDeclaration();
    }

    private boolean isAugmentationApplicable(BinaryExpr.Operator op) {
        switch (op) {
            case LESS:
            case LESS_EQUALS:
            case GREATER:
            case GREATER_EQUALS:
            case EQUALS:
            case NOT_EQUALS:
            case OR:
            case AND:
            case PLUS:
            case MULTIPLY:
                return true;
            default:
                return false;
        }
    }

}
