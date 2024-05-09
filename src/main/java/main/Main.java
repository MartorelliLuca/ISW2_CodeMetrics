package main;

import enums.ProjectEnum;
import exceptions.ProportionException;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.IOException;
import java.net.URISyntaxException;

public class Main {

    public static final String PROJECT_NAME = "bookkeeper";
    private static final String PROJECT_PATH = "/home/lux/Documents/GitHub/";

    public static void main(String[] args) throws IOException, URISyntaxException, GitAPIException, ProportionException {
        for (ProjectEnum project : ProjectEnum.values()) {
            ExecutionFlow.execute(PROJECT_PATH, project.name().toLowerCase());
        }
    }
}