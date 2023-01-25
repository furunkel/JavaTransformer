package javaaug.transformations;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.visitor.TreeVisitor;
import javaaug.Transformation;

import java.util.*;


public class ShuffleVariableNames extends Transformation<ShuffleVariableNames.Site> {
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

    public ShuffleVariableNames(MethodDeclaration methodDeclaration, Random random) {
        super(methodDeclaration);
        this.random = random;
    }

    public static class Builder extends Transformation.Builder<ShuffleVariableNames> {
        private Random random = new Random();

        public Random getRandom() {
            return random;
        }

        public void setRandom(Random random) {
            this.random = random;
        }

        public ShuffleVariableNames build(MethodDeclaration methodDeclaration) {
        return new ShuffleVariableNames(methodDeclaration, random);
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
        List<String> variableNamesList = new ArrayList<>(variableNames);
        List<String> shuffledVariableNamesList = new ArrayList<>(variableNamesList);
        Collections.shuffle(shuffledVariableNamesList, random);
        Map<String, String> map = new HashMap<>();
        for(int i = 0; i < variableNamesList.size(); i++) {
            map.put(variableNamesList.get(i), shuffledVariableNamesList.get(i));
        }

        TreeVisitor treeVisitor = new TreeVisitor() {
            @Override
            public void process(Node node) {
                if (node instanceof SimpleName
                        && !(node.getParentNode().orElse(null) instanceof MethodDeclaration)
                        && !(node.getParentNode().orElse(null) instanceof ClassOrInterfaceDeclaration)) {
                    SimpleName simpleName = (SimpleName) node;
                    String name = simpleName.getIdentifier();
                    String newVariableName = map.getOrDefault(name, null);
                    if(newVariableName != null) {
                        simpleName.setIdentifier(newVariableName);
                    }
                } else if(node instanceof SimpleName) {
                    System.out.println("Not renaming " + ((SimpleName) node).getIdentifier());
                }
            }
        };

        treeVisitor.visitPreOrder(getMethodDeclaration());
//        for(Parameter parameter: getMethodDeclaration().getParameters()) {
//            System.out.println(parameter.getNameAsString() + " " + map.get(parameter.getNameAsString()));
//            treeVisitor.process(parameter.getName());
//        }

        System.out.println(map);

//        if(site.getVariableA() instanceof Parameter) {
//            site.getVariableA().getName().setIdentifier(identifierB);
//        }
//        if(site.getVariableB() instanceof Parameter) {
//            site.getVariableB().getName().setIdentifier(identifierA);
//        }

    }
}
