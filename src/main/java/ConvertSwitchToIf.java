import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.visitor.TreeVisitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class ConvertSwitchToIf extends Transformation<Node> {

    public ConvertSwitchToIf(MethodDeclaration methodDeclaration) {
        super(methodDeclaration);
    }

    @Override
    public List<Node> getSites() {
        return locateConditionals(getMethodDeclaration());
    }

    private List<Node> locateConditionals(MethodDeclaration methodDeclaration) {
        List<Node> switchNodes = new ArrayList<>();

        new TreeVisitor() {
            @Override
            public void process(Node node) {
                if (node instanceof SwitchStmt) {
                    switchNodes.add(node);
                }
            }
        }.visitPreOrder(methodDeclaration);
        return switchNodes;
    }

    public MethodDeclaration transform(Node switchNode) {
        new TreeVisitor() {
            @Override
            public void process(Node node) {
                if (node.equals(switchNode)) {
                    ArrayList<Object> ifStmts = new ArrayList<>();
                    NodeList<SwitchEntry> entries = ((SwitchStmt) node).getEntries();

                    if (entries.size() == 0) {
                        // empty
                        ifStmts.add(getIfStmt(node, null, Collections.emptyList()));
                    } else {
                        BlockStmt defaultBlockStmt = null;
                        boolean onlyDefault = entries.size() == 1;
                        for(int i = 0; i < entries.size(); i++) {
                            SwitchEntry switchEntry = entries.get(i);
                            List<SwitchEntry> fallThroughEntries = new ArrayList<>();

                            boolean isDefault = switchEntry.getLabels().isEmpty();
                            if(!isDefault) {
                                boolean hasBreak = switchEntry.findFirst(BreakStmt.class).isPresent();
                                if(!hasBreak) {
                                    for(int j = i + 1; j < entries.size(); j++) {
                                        SwitchEntry fallthroughEntry = entries.get(j);
                                        fallThroughEntries.add(fallthroughEntry);
                                        if(fallthroughEntry.findFirst(BreakStmt.class).isPresent()) break;
                                    }
                                }
                                ifStmts.add(getIfStmt(node, switchEntry, fallThroughEntries));
                            } else {
                                if (onlyDefault) {
                                    // default without cases
                                    ifStmts.add(getIfStmt(node, switchEntry, Collections.emptyList()));
                                } else {
                                    // default with cases
                                    defaultBlockStmt = getBlockStmt(switchEntry, Collections.emptyList());
                                }
                            }
                        }
                        if (defaultBlockStmt != null) ifStmts.add(defaultBlockStmt); // default at end with cases
                        for (int i = 0; i < ifStmts.size() - 1; i++) {
                            ((IfStmt) ifStmts.get(i)).setElseStmt((Statement) ifStmts.get(i + 1));
                        }
                    }
                    node.replace((IfStmt) ifStmts.get(0));
                }
            }
        }.visitPreOrder(getMethodDeclaration());
        return getMethodDeclaration();
    }

    private Expression getBinaryExpr(Node switchNode, SwitchEntry switchEntry) {
        BinaryExpr binaryExpr = new BinaryExpr();
        binaryExpr.setLeft(((SwitchStmt) switchNode).getSelector());
        binaryExpr.setOperator(BinaryExpr.Operator.EQUALS);
        if (switchEntry != null && switchEntry.getLabels().size() != 0) {
            binaryExpr.setRight(switchEntry.getLabels().get(0)); // case(?)
        } else {
            binaryExpr.setRight(((SwitchStmt) switchNode).getSelector()); // only default
        }
        return binaryExpr;
    }

    private BlockStmt getBlockStmt(SwitchEntry switchEntry, List<SwitchEntry> fallthroughEntries) {
        BlockStmt blockStmt = new BlockStmt();

        if (switchEntry != null) {
            Stream<Statement> statements = Stream.concat(switchEntry.getStatements().stream(),
                           fallthroughEntries.stream().flatMap((entry) -> entry.getStatements().stream() ));
            statements.forEach((stmt) -> {
                if (!(stmt instanceof BreakStmt)) blockStmt.addStatement(stmt);
            });
        }
        return blockStmt;
    }

    private IfStmt getIfStmt(Node switchNode, SwitchEntry switchEntry, List<SwitchEntry> fallthroughEntries) {
        IfStmt ifStmt = new IfStmt();
        ifStmt.setCondition(getBinaryExpr(switchNode, switchEntry));
        ifStmt.setThenStmt(getBlockStmt(switchEntry, fallthroughEntries));
        return ifStmt;
    }

}
