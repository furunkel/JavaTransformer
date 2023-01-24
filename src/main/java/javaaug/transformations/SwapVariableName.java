package javaaug.transformations;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.visitor.TreeVisitor;
import javaaug.Transformation;

import java.util.*;


public class SwapVariableName extends Transformation<SwapVariableName.Site> {
    private final Random random;

    public static class Site extends Transformation.Site {
        public final Set<String> variableNames;

        public Set<String> getVariableNames() {
            return variableNames;
        }

        public Site(Set<String> variableNames) {
            this.variableNames = variableNames;
        }
    }

    // public javaaug.transformations.SwapVariableName(MethodDeclaration methodDeclaration, Random random) {
    //     super(methodDeclaration);
    //     mRandom = random;
    // }

    // public javaaug.transformations.SwapVariableName(MethodDeclaration methodDeclaration) {
    //     this(methodDeclaration, new Random());
    // }

    public SwapVariableName(MethodDeclaration methodDeclaration, Random random) {
        super(methodDeclaration);
        this.random = random;
    }

    public static class Builder extends Transformation.Builder<SwapVariableName> {
        private Random random = new Random();

        public Random getRandom() {
            return random;
        }

        public void setRandom(Random random) {
            this.random = random;
        }

        public SwapVariableName build(MethodDeclaration methodDeclaration) {
        return new SwapVariableName(methodDeclaration, random);
      }
    }
    
    @Override
    public List<Site> getSites() {
        List<Site> sites = new ArrayList<>();

        List<Node> variableNodes = new ArrayList<Node>();
        // variableNodes.addAll(getMethodDeclaration().findAll(Parameter.class));
        variableNodes.addAll(getMethodDeclaration().findAll(VariableDeclarator.class));
        variableNodes.addAll(getMethodDeclaration().getParameters());

        // List<Node> permutation = Collections.shuffle(variableNodes, mRandom);

        Set<String> variableNames = new HashSet<>();

        for(Node variableA: variableNodes) {
            BlockStmt blockA;
            if(variableA instanceof Parameter) {
                variableNames.add(((Parameter) variableA).getNameAsString());
            } else {
                VariableDeclarator variableDeclarator = (VariableDeclarator) variableA;
                variableNames.add(variableDeclarator.getNameAsString());
            }
        }

        sites.add(new Site(variableNames));

        return sites;
    }


    public void transform(Site site) {

        Set<String> variableNames = site.getVariableNames();
        Map<String, String> map = new HashMap<>();

        List<String> variableNamesList = new ArrayList<>(variableNames);
        Collections.shuffle(variableNamesList, random);

        new TreeVisitor() {
            @Override
            public void process(Node node) {
                if (node instanceof SimpleName
                        && !(node.getParentNode().orElse(null) instanceof MethodDeclaration)
                        && !(node.getParentNode().orElse(null) instanceof ClassOrInterfaceDeclaration)) {
                    SimpleName simpleName = (SimpleName) node;
                    String name = simpleName.getIdentifier();
                    if(variableNames.contains(name)) {
                        String tmpVariableName = map.compute(name, (k,v) -> v == null ? variableNamesList.get(map.size()) : v);
                        simpleName.setIdentifier(tmpVariableName);
                    }
                }
            }
        }.visitPreOrder(getMethodDeclaration());
    }
}
