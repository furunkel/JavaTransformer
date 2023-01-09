import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.lang.Exception;


import com.github.javaparser.ast.body.MethodDeclaration;

public abstract class Transformation<T> {
    public static class TransformationsExhaustedException extends Exception {
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

    public MethodDeclaration transformRandom(double transformationProbability) {
        return transformRandom(transformationProbability, new Random());
    }

    public MethodDeclaration transformRandom(double transformationProbability, Random random) {
        for(T n: mSites) {
            if(random.nextFloat() > transformationProbability) {
                transform(n);
            }
        }
        return mMethodDeclaration;
    }

    public MethodDeclaration transform() throws TransformationsExhaustedException {
        if(mIter == null) {
            mIter = mSites.iterator();
        }

        try {
            return transform(mIter.next());
        } catch(NoSuchElementException e) {
            throw new TransformationsExhaustedException();
        }
    }

    public Transformation<T> prepare() {
        mSites = getSites();
        return this;
    }

    public abstract List<T> getSites();
    public abstract MethodDeclaration transform(T site);
}
