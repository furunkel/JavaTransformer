import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;

import java.util.ArrayList;
import java.util.List;

public class InsertLogStatement extends Transformation<InsertLogStatement.Site> {

    private final List<String> mStrings;

    public InsertLogStatement(MethodDeclaration methodDeclaration, List<String> strings) {
        super(methodDeclaration);
        this.mStrings = strings;
    }

    public static class Site {
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

    public MethodDeclaration transform(Site site) {
        MethodDeclaration methodDeclaration = getMethodDeclaration();
        System.out.println(methodDeclaration.getBody().get().getStatements().contains(site.getStatement()));
        methodDeclaration.getBody().get().getStatements().addAfter(getLogStatement(site.getString()), site.getStatement());
        return methodDeclaration;
    }

    private Statement getLogStatement(String string) {
        String logStr = "System.out.println(\"" + string + "\");";
        return StaticJavaParser.parseStatement(logStr);
    }
}
