package javaaug.transformations;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.Statement;
import javaaug.Transformation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InsertLogStatement extends Transformation<InsertLogStatement.Site> {

    private final List<String> mStrings;


    public static class Builder extends Transformation.Builder<InsertLogStatement> {
        private static final List<String> DEFAULT_STRINGS = Arrays.asList("log");
        private List<String> strings = DEFAULT_STRINGS;

        public List<String> getStrings() {
            return strings;
        }

        public void setStrings(List<String> strings) {
            this.strings = strings;
        }

        @Override
        public InsertLogStatement build(MethodDeclaration methodDeclaration) {
            return new InsertLogStatement(methodDeclaration, strings);
        }
    }

    public InsertLogStatement(MethodDeclaration methodDeclaration, List<String> strings) {
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
        MethodDeclaration methodDeclaration = getMethodDeclaration();
        System.out.println(methodDeclaration.getBody().get().getStatements().contains(site.getStatement()));
        methodDeclaration.getBody().get().getStatements().addAfter(getLogStatement(site.getString()), site.getStatement());
    }

    private Statement getLogStatement(String string) {
        String logStr = "System.out.println(\"" + string + "\");";
        return StaticJavaParser.parseStatement(logStr);
    }
}
