package flows;

public class ExecutionFlow {

    private ExecutionFlow() {}

    public static void execute(String projectPath, String projectName) throws Exception {
        int maxIndex = RetrievingFlow.retrieve(projectPath, projectName);

        WekaFlow.weka(projectName, maxIndex) ;
    }


}