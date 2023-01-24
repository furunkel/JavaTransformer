package javaaug.transformations;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.Statement;
import javaaug.Transformation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InsertComment extends Transformation<InsertComment.Site> {

    private final List<String> mStrings;

    public static class Builder extends Transformation.Builder<InsertComment> {
        private static final List<String> DEFAULT_STRINGS = Arrays.asList("comment");
        private List<String> strings = DEFAULT_STRINGS;

        public List<String> getStrings() {
            return strings;
        }

        public void setStrings(List<String> strings) {
            this.strings = strings;
        }

        @Override
        public InsertComment build(MethodDeclaration methodDeclaration) {
            return new InsertComment(methodDeclaration, strings);
        }
    }

    public InsertComment(MethodDeclaration methodDeclaration, List<String> strings) {
        super(methodDeclaration);
        this.mStrings = strings;
    }

    public static final class Site extends Transformation.Site {
        public final String string;
        public final Statement statement;

        public Site(String string, Statement statement) {
            this.string = string;
            this.statement = statement;
        }
        public String getString() {
            return string;
        }
        public Statement getStatement() {
            return statement;
        }
    }

    @Override
    public List<Site> getSites() {
        ArrayList<Site> sites = new ArrayList<Site>();

        for(String s: mStrings) {
            for(Statement stmt: getMethodStatements()) {
                sites.add(new Site(s, stmt));
            }
        }
        return sites;
    }

    public void transform(Site site) {
        site.getStatement().setLineComment(site.getString());
    }
}
