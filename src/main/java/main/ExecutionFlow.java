package main;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ExecutionFlow {

    private ExecutionFlow() {}

    public static void execute(String projectPath, String projectName) throws Exception {
        int maxIndex = RetrievingFlow.retrieve(projectPath, projectName);

        WekaFlow.weka(projectName, maxIndex) ;
    }


}