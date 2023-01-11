import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.visitor.TreeVisitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class SwapVariableName extends Transformation<SwapVariableName.Site> {
    public static class Site {
        public final NodeWithSimpleName<? extends Node> variableA;
        public final NodeWithSimpleName<? extends Node> variableB;
        public final BlockStmt blockStmt;

        public NodeWithSimpleName<? extends Node> getVariableA() {
            return variableA;
        }

        public NodeWithSimpleName<? extends Node> getVariableB() {
            return variableB;
        }

        public BlockStmt getBlockStmt() {
            return blockStmt;
        }
        
        public Site(NodeWithSimpleName<? extends Node> variableA, NodeWithSimpleName<? extends Node> variableB, BlockStmt blockStmt) {
            this.variableA = variableA;
            this.variableB = variableB;
            this.blockStmt = blockStmt;
        }
    }

    // public SwapVariableName(MethodDeclaration methodDeclaration, Random random) {
    //     super(methodDeclaration);
    //     mRandom = random;
    // }

    // public SwapVariableName(MethodDeclaration methodDeclaration) {
    //     this(methodDeclaration, new Random());
    // }

    public SwapVariableName(MethodDeclaration methodDeclaration) {
        super(methodDeclaration);
    }

    @Override
    public List<Site> getSites() {
        List<Site> sites = new ArrayList<>();

        List<Node> variableNodes = new ArrayList<Node>();
        // variableNodes.addAll(getMethodDeclaration().findAll(Parameter.class));
        variableNodes.addAll(getMethodDeclaration().findAll(VariableDeclarator.class));
        variableNodes.addAll(getMethodDeclaration().getParameters());

        // List<Node> permutation = Collections.shuffle(variableNodes, mRandom);

        for(Node variableA: variableNodes) {
            BlockStmt blockA;
            if(variableA instanceof Parameter) {
                blockA = getMethodDeclaration().getBody().get();
            } else {
                blockA = variableA.findAncestor(BlockStmt.class).orElse(null);
            }

            for(Node variableB: variableNodes) {
                BlockStmt blockB;
                if(variableB instanceof Parameter) {
                    blockB = getMethodDeclaration().getBody().get();
                } else {
                    blockB = variableB.findAncestor(BlockStmt.class).orElse(null);
                }

                if(blockA.equals(blockB)) {
                    sites.add(new Site((NodeWithSimpleName<Node>)variableA, (NodeWithSimpleName<Node>)variableB, blockA));
                }
            }
        }
        return sites;
    }


    public MethodDeclaration transform(Site site) {

        String identifierA = site.getVariableA().getNameAsString();
        String identifierB = site.getVariableB().getNameAsString();

        if(identifierA.equals(identifierB)) {
            return getMethodDeclaration();
        }

        // System.out.println(identifierA + "->" + identifierB);
        // System.out.println(site.getBlockStmt().toString());
        // System.out.println("\n\n\n\n");

        BlockStmt blockStmt = site.getBlockStmt();

        new TreeVisitor() {
            public void visitPreOrder(Node node) {
                process(node);
                if(node instanceof BlockStmt && !node.equals(blockStmt)) return;
                new ArrayList<>(node.getChildNodes()).forEach(this::visitPreOrder);
            }
            
            @Override
            public void process(Node node) {
                if (node instanceof SimpleName
                        && !(node.getParentNode().orElse(null) instanceof MethodDeclaration)
                        && !(node.getParentNode().orElse(null) instanceof ClassOrInterfaceDeclaration)) {
                    SimpleName simpleName = (SimpleName) node;
                    if(simpleName.getIdentifier().equals(identifierA)) {
                        simpleName.setIdentifier(identifierB);
                    } else if(simpleName.getIdentifier().equals(identifierB)) {
                        simpleName.setIdentifier(identifierA);
                    }
                }
            }
        }.visitPreOrder(blockStmt);

        // parameters are not inside blockStmt so need to be handled separately.
        if(site.getVariableA() instanceof Parameter) {
            site.getVariableA().getName().setIdentifier(identifierB);
        }
        if(site.getVariableB() instanceof Parameter) {
            site.getVariableB().getName().setIdentifier(identifierA);
        }

        // return com;

        return getMethodDeclaration();
    }
}
