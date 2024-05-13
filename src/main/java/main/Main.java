package main;

import flows.ExecutionFlow;

import java.nio.file.Path;

public class Main {

    private static final Path PROJECT_PATH = Path.of("/home/lux/Documents/GitHub/") ;
    private enum ProjectEnum {
        BOOKKEEPER,
        OPENJPA
    }
    public static void main(String[] args) throws Exception {
        for (ProjectEnum project : ProjectEnum.values()) {
            ExecutionFlow.execute(PROJECT_PATH.toString(), project.name().toLowerCase());
        }
    }
}