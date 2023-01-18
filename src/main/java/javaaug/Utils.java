package javaaug;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;

import java.io.*;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

public final class Utils {

    public static CompilationUnit parse(File javaFile) throws IOException {
        return parse(javaFile, null);
    }

    public static CompilationUnit parse(File javaFile, String javaCode) throws IOException {
        CombinedTypeSolver combinedSolver = new CombinedTypeSolver
                (
                        new JavaParserTypeSolver(javaFile.getAbsoluteFile().getParentFile()),
                        new ReflectionTypeSolver()
                );

        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedSolver);
        StaticJavaParser.getParserConfiguration().setSymbolResolver(symbolSolver);

        CompilationUnit cu;
        StaticJavaParser.getParserConfiguration().setAttributeComments(false);
        if(javaCode == null) javaCode = new String(Files.readAllBytes(javaFile.toPath()));
        cu = StaticJavaParser.parse(javaCode);
        return cu;
    }


    private static boolean isTransformable(MethodDeclaration methodDeclaration) {
        return !methodDeclaration.getAnnotationByClass(DoNotTransform.class).isPresent();
    }

    public static List<MethodDeclaration> findTransformableMethodDeclarations(CompilationUnit cu) {
        return cu.findAll(MethodDeclaration.class).stream().filter(Utils::isTransformable).collect(Collectors.toList());
    }

//    private static boolean didCodeChange(CompilationUnit bRoot, CompilationUnit aRoot) {
//        MethodDeclaration mdBefore = (MethodDeclaration) (bRoot.getChildNodes().get(0)).getChildNodes().get(1);
//        String mdBeforeStr = mdBefore.toString().replaceAll("\\s+", "");
//        MethodDeclaration mdAfter = (MethodDeclaration) (aRoot.getChildNodes().get(0)).getChildNodes().get(1);
//        String mdAfterStr = mdAfter.toString().replaceAll("\\s+", "");
//        return mdBeforeStr.compareTo(mdAfterStr) != 0;
//    }

    public static void saveTransformation(String savePath, CompilationUnit aRoot, File javaFile, String place) {
//        String output_dir = savePath + javaFile.getPath().replaceFirst(Utils.mRootInputPath, "");
//        output_dir = output_dir.substring(0, output_dir.lastIndexOf(".java")) + "_" + place + ".java";
//        MethodDeclaration mdAfter = (MethodDeclaration) (aRoot.getChildNodes().get(0)).getChildNodes().get(1);
//        writeSourceCode(mdAfter, output_dir);
    }

    private static void writeSourceCode(MethodDeclaration md, String codePath) {
        File targetFile = new File(codePath).getParentFile();
        if (targetFile.exists() || targetFile.mkdirs()) {
            try (PrintStream ps = new PrintStream(codePath)) {
                String tfSourceCode = md.toString();
                ps.println(tfSourceCode);
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static boolean isNotPermeableStatement(Node node) {
        return (node instanceof EmptyStmt
                || node instanceof LabeledStmt
                || node instanceof BreakStmt
                || node instanceof ContinueStmt
                || node instanceof ReturnStmt);
    }
}
