package main;

import enums.ProjectEnum;

import java.nio.file.Path;

public class Main {

    public static final String PROJECT_NAME = "bookkeeper";
    /*private static final String PROJECT_PATH = "/home/lux/Documents/GitHub/";*/

    private static final Path PROJECT_PATH = Path.of("/home/lux/Documents/GitHub/") ;

    public static void main(String[] args) throws Exception {
        for (ProjectEnum project : ProjectEnum.values()) {
            ExecutionFlow.execute(PROJECT_PATH.toString(), project.name().toLowerCase());
        }
    }
}