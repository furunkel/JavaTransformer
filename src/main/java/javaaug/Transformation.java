package javaaug;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.lang.Exception;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.Statement;


public abstract class Transformation<T extends Transformation.Site> {
    public static class TransformationsExhaustedException extends Exception {
    }

    public static abstract class Site {
    }

    public static abstract class Builder<T extends Transformation> {
        public abstract T build(MethodDeclaration methodDeclaration);
    }

    public static final class NodeSite extends Site {
        public final Node node;

        public NodeSite(Node node) {
            this.node = node;
        }

        public Node getNode() {
            return node;
        }
    }

    public static final class StatementSite extends Site {
        public final Statement statement;

        public StatementSite(Statement statement) {
            this.statement = statement;
        }

        public Statement getStatement() {
            return statement;
        }
    }

    public static final class ValueSite<T> extends Site {
        public final T value;

        public ValueSite(T value) {
            this.value = value;
        }

        public T getValue() {
            return value;
        }
    }


    private final MethodDeclaration mMethodDeclaration;
    private List<T> mSites;
    private Iterator<T> mIter;

    public Transformation(MethodDeclaration methodDeclaration) {
        this.mMethodDeclaration = methodDeclaration;
    }

    public MethodDeclaration getMethodDeclaration() {
        return mMethodDeclaration;
    }

    public MethodDeclaration transformAll() {
        for(T n: mSites) {
            transform(n);
        }
        return mMethodDeclaration;
    }

    public void transformRandom(double transformationProbability) {
        transformRandom(transformationProbability, new Random());
    }

    public void transformRandom(double transformationProbability, Random random) {
        for(T n: mSites) {
            if(random.nextFloat() < transformationProbability) {
                transform(n);
            }
        }
    }

    public void transform() throws TransformationsExhaustedException {
        if(mIter == null) {
            mIter = mSites.iterator();
        }

        try {
            transform(mIter.next());
        } catch(NoSuchElementException e) {
            throw new TransformationsExhaustedException();
        }
    }

    public Transformation<T> prepare() {
        mSites = getSites();
        return this;
    }

    public List<Statement> getMethodStatements() {
        List<Statement> sites = new ArrayList<>();
        MethodDeclaration methodDeclaration = getMethodDeclaration();
        if (methodDeclaration.getBody().isPresent()) {
            sites.addAll(methodDeclaration.getBody().get().getStatements());
        }
        return sites;
    }

    public abstract List<T> getSites();
    public abstract void transform(T site);
}
