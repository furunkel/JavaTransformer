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
        public final float location;

        public Site(String string, float location) {
            this.string = string;
            this.location = location;
        }
        public String getString() {
            return string;
        }
        public float getLocation() {
            return location;
        }
    }

    @Override
    public List<Site> getSites() {
        ArrayList<Site> sites = new ArrayList<Site>();

        for(String s: mStrings) {
            for(float i = 0; i <= 1.1; i += 0.1f) {
                sites.add(new Site(s, i));
            }
        }
        return sites;
    }

    public MethodDeclaration transform(Site site) {
        MethodDeclaration methodDeclaration = getMethodDeclaration();

        BlockStmt blockStmt = new BlockStmt();
        for (Statement statement : methodDeclaration.getBody().get().getStatements()) {
            blockStmt.addStatement(statement);
        }
        int place = (int) (site.getLocation() * blockStmt.getStatements().size());
        blockStmt.addStatement(place, getLogStatement(site.getString()));
        methodDeclaration.setBody(blockStmt);
        return methodDeclaration;
    }

    private Statement getLogStatement(String string) {
        String logStr = "System.out.println(\"" + string + "\");";
        return StaticJavaParser.parseStatement(logStr);
    }
}
