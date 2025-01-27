package javaaug;

import org.apache.commons.io.FileUtils;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import javaaug.transformations.LowerNegation;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Main {
    static String mRootInputPath = "";
    static String mRootOutputPath = "";

    public static void main(String[] args) {
        /* arg[0]: root path for input folder, ie, ~/data/methods/
         * arg[1]: root path for output folder, ie, ~/data/transforms/
         *
         * extracted single method of project should be in 'methods' folder
         * separate folder for each refactoring will be created in 'transforms' folder
         */

        if (args.length == 2) {
            String inputPath = args[0];
            if (!inputPath.endsWith("/")) {
                inputPath += "/";
            }
            mRootInputPath = inputPath;

            String outputPath = args[1];
            if (!outputPath.endsWith("/")) {
                outputPath += "/";
            }
            mRootOutputPath = outputPath;

            inspectDataset();
        } else {
            String msg = "Error (args):" +
                    "\n\targ[0]: root path for input folder" +
                    "\n\targ[1]: root path for output folder";
            System.out.println(msg);
        }
    }

    private static void inspectDataset() {
        String input_dir = mRootInputPath;
        ArrayList<File> javaFiles = new ArrayList<>(
                FileUtils.listFiles(
                        new File(input_dir),
                        new String[]{"java"},
                        true)
        );

        javaFiles.stream().forEach((javaFile) -> {
            try {

        // mSavePath = javaaug.Common.mRootOutputPath + this.getClass().getSimpleName() + "/";
                CompilationUnit root = Utils.parse(javaFile);
                List<MethodDeclaration> methodDeclarations = Utils.findTransformableMethodDeclarations((root));
                
                methodDeclarations.stream().forEach((methodDeclaration) -> {
                    // new BooleanExchange(methodDeclaration).prepare().transformRandom(0.5);
                    // new javaaug.transformations.InsertLogStatement(methodDeclaration, Arrays.asList("log", "test")).prepare().transformRandom(0.5);
                    // new javaaug.transformations.InsertComment(methodDeclaration, Arrays.asList("log", "test")).prepare().transformRandom(0.5);
                    // // new LoopExchange(methodDeclaration).prepare().transformRandom(0.5);
                    // // new javaaug.transformations.PermuteStatement(methodDeclaration).prepare().transformRandom(0.5);
//                     new javaaug.transformations.ReorderExpression(methodDeclaration).prepare().transformRandom(0.5);
                     new javaaug.transformations.ConvertSwitchToIf(methodDeclaration).prepare().transformAll();
                    // new javaaug.transformations.WrapInTryCatch(methodDeclaration).prepare().transformRandom(0.5);
                    // new javaaug.transformations.InsertUnusedStatement(methodDeclaration).prepare().transformRandom(0.5);
                    // new javaaug.transformations.SwapVariableName(methodDeclaration).prepare().transformRandom(0.5);
                    // new javaaug.transformations.ConvertAndConditionToNestedIf(methodDeclaration).prepare().transformRandom(0.5);
                    // new javaaug.transformations.SwapIfElseBranches(methodDeclaration).prepare().transformRandom(0.5);
//                    new LowerNegation(methodDeclaration).prepare().transformRandom(0.5);
                    System.out.println(methodDeclaration);
                });

                // new VariableRenaming().inspectSourceCode(javaFile);
                // new BooleanExchange().inspectSourceCode(javaFile);
                // new LoopExchange().inspectSourceCode(javaFile);
                // new SwitchToIf().inspectSourceCode(javaFile);
                // new ReorderCondition().inspectSourceCode(javaFile);
                // new javaaug.transformations.PermuteStatement().inspectSourceCode(javaFile);
                // new UnusedStatement().inspectSourceCode(javaFile);
                // new LogStatement().inspectSourceCode(javaFile);
                // new TryCatch().inspectSourceCode(javaFile);
            } catch (Exception ex) {
                System.out.println("Exception: " + javaFile);
                ex.printStackTrace();
            }
        });

        
    }
}
