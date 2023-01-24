package javaaug.transformations;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.visitor.TreeVisitor;
import javaaug.Transformation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ReorderExpression extends Transformation<Transformation.NodeSite> {

    public ReorderExpression(MethodDeclaration methodDeclaration) {
        super(methodDeclaration);
    }


    public static class Builder extends Transformation.Builder<ReorderExpression> {
      public ReorderExpression build(MethodDeclaration methodDeclaration) {
        return new ReorderExpression(methodDeclaration);
      }
    }

    @Override
    public List<NodeSite> getSites() {
        List<NodeSite> operatorNodes = new ArrayList<>();
        new TreeVisitor() {
            @Override
            public void process(Node node) {
                if (node instanceof BinaryExpr && isAugmentationApplicable(((BinaryExpr) node))) {
                    operatorNodes.add(new NodeSite(node));
                }
            }
        }.visitPreOrder(getMethodDeclaration());
        return operatorNodes;
    }

    public void transform(NodeSite site) {
        Node node = site.getNode();
        BinaryExpr replNode = (BinaryExpr) node.clone();
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

    private boolean notAStringOperation(BinaryExpr opNode) {
        //FIXME: this is not sufficient. Check if any String variables are involved.
        if (opNode.findFirst(StringLiteralExpr.class).isPresent()) return false;

        return !Objects.equals(opNode.calculateResolvedType().describe(), "java.lang.String");
    }

    private boolean isAugmentationApplicable(BinaryExpr opNode) {
        BinaryExpr.Operator op = opNode.getOperator();
        switch (op) {
            case LESS:
            case LESS_EQUALS:
            case GREATER:
            case GREATER_EQUALS:
            case EQUALS:
            case NOT_EQUALS:
            case OR:
            case AND:
            case MULTIPLY:
                return true;
            case PLUS:
                return notAStringOperation(opNode);
            default:
                return false;
        }
    }

}
