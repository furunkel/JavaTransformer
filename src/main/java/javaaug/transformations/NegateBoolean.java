package javaaug.transformations;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.visitor.TreeVisitor;
import javaaug.Transformation;

import java.util.ArrayList;
import java.util.Objects;
import java.util.List;
import java.util.stream.Collectors;

public class NegateBoolean extends Transformation<Transformation.NodeSite> {
    // public void inspectSourceCode(File javaFile) {
    //     this.mJavaFile = javaFile;
    //     mSavePath = javaaug.Common.mRootOutputPath + this.getClass().getSimpleName() + "/";
    //     CompilationUnit root = javaaug.Common.getParseUnit(mJavaFile);
    //     if (root != null) {
    //         this.visit(root.clone(), null);
    //     }
    // }

    public NegateBoolean(MethodDeclaration methodDeclaration) {
        super(methodDeclaration);
    }

    @Override
    public List<NodeSite> getSites() {
        return locateBooleanVariables(getMethodDeclaration()).stream().map(NodeSite::new).collect(Collectors.toList());
    }

    @Override
    public void transform(NodeSite site) {
        Node bolNode = site.getNode();
        MethodDeclaration methodDeclaration = getMethodDeclaration();
        new TreeVisitor() {
            @Override
            public void process(Node node) {
                if (node != null && node.toString().equals(bolNode.toString())) {
                    if (node instanceof NameExpr) {
                        if (node.getParentNode().orElse(null) instanceof UnaryExpr) {
                            // i.e. !x -> x
                            node.getParentNode().orElse(null).replace(node);
                        } else if (node.getParentNode().orElse(null) instanceof BinaryExpr
                                || node.getParentNode().orElse(null) instanceof Statement
                                || node.getParentNode().orElse(null) instanceof VariableDeclarator
                                || node.getParentNode().orElse(null) instanceof MethodCallExpr) {
                            // i.e. x == true -> !x == true; call(x) -> call(!x)
                            ((NameExpr) node).setName(getNotExpStr(node));
                        } else if (node.getParentNode().orElse(null) instanceof AssignExpr) {
                            AssignExpr parNode = (AssignExpr) node.getParentNode().orElse(null);
                            if (parNode.getValue().toString().equals(bolNode.toString())) {
                                // i.e. y = x; -> y = !x;
                                parNode.setValue(StaticJavaParser.parseExpression(getNotExpStr(parNode.getValue())));
                            } else if (parNode.getTarget().toString().equals(bolNode.toString())) {
                                // i.e. x = r() && x; -> x = !(r() && !x);
                                new TreeVisitor() {
                                    @Override
                                    public void process(Node node) {
                                        if (node != null && node.toString().equals(bolNode.toString())) {
                                            if (node.getParentNode().orElse(null) instanceof UnaryExpr) {
                                                node.getParentNode().orElse(null).replace(node);
                                            } else if (node instanceof NameExpr) {
                                                ((NameExpr) node).setName(getNotExpStr(node));
                                            }
                                        }
                                    }
                                }.visitPreOrder(parNode.getValue());
                                parNode.setValue(StaticJavaParser.parseExpression(getNotExpStr(parNode.getValue())));
                            }
                        }
                    } else if (node instanceof SimpleName) {
                        if (node.getParentNode().isPresent() && node.getParentNode().orElse(null) instanceof VariableDeclarator) {
                            VariableDeclarator parNode = (VariableDeclarator) node.getParentNode().orElse(null);
                            if (parNode.getName().asString().equals(bolNode.toString()) && parNode.getInitializer().isPresent()) {
                                //i.e. boolean x = true; -> boolean x = false;
                                Expression expVal = parNode.getInitializer().get();
                                expVal.replace(StaticJavaParser.parseExpression(getNotExpStr(expVal)));
                            }
                        }
                    }
                }
            }
        }.visitPreOrder(methodDeclaration);
    }

    private ArrayList<Node> locateBooleanVariables(Node node) {
        ArrayList<Node> booleanNodes = new ArrayList<>();
        new TreeVisitor() {
            @Override
            public void process(Node node) {
                Node booleanNode = getBooleanVariable(node);
                if (booleanNode != null) {
                    booleanNodes.add(booleanNode);
                }
            }
        }.visitPreOrder(node);
        return booleanNodes;
    }

    private String getNotExpStr(Node node) {
        if (node instanceof BooleanLiteralExpr) {
            boolean val = !((BooleanLiteralExpr) node).getValue();
            return String.valueOf(val);
        } else {
            String expStr = "!";
            if (node.toString().length() > 1) {
                expStr += "(" + node + ")";
            } else {
                expStr += node;
            }
            return expStr;
        }
    }

    private Node getBooleanVariable(Node node) {
        if (node.toString().equalsIgnoreCase(PrimitiveType.booleanType().asString())
                && node.getParentNode().orElse(null) instanceof VariableDeclarator) {
            VariableDeclarator parentNode = (VariableDeclarator) node.getParentNode().get();
            if (parentNode.getInitializer().isPresent()) {
                for (SimpleName sn : Objects.requireNonNull(node.getParentNode()
                        .orElse(null)).findAll(SimpleName.class)) {
                    if (!sn.toString().equalsIgnoreCase(PrimitiveType.booleanType().asString())) {
                        return sn;
                    }
                }
            }
        }
        return null;
    }
}
