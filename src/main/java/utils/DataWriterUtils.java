package utils;


import model.retrieve.ClassInfo;

import java.util.ArrayList;
import java.util.List;

public class DataWriterUtils {

    public static final List<String> ATTRIBUTES_LIST = new ArrayList<>(List.of(
            "LinesOfCode",
            "AddedLOC",
            "MaxAddedLOC",
            "AvgAddedLOC",
            //"RemovedLoc",
            //"MaxRemovedLoc",
            //"AvgRemovedLoc",
            "TouchedLOC",
            "Churn",
            "MaxChurn",
            "AvgChurn",
            "NumberOfAuthors",
            "NumberOfRevisions",
            "NumberOfDefectsFixed",
            "Buggy")) ;



    public static String buildClassDataString(ClassInfo classInfo, String separator) {

        return classInfo.getLoc()
                + separator
                + classInfo.getAddedLoc()
                + separator
                + classInfo.getMaxAddedLoc()
                + separator
                + classInfo.getAvgAddedLoc()
                + separator
                /*
                + classInfo.getRemovedLoc()
                + separator
                + classInfo.getMaxRemovedLoc()
                + separator
                + classInfo.getAvgRemovedLoc()
                + separator
                 */
                + classInfo.getTouchedLoc()
                + separator
                + classInfo.getChurn()
                + separator
                + classInfo.getMaxChurn()
                + separator
                + classInfo.getAvgChurn()
                + separator
                + classInfo.getNumberOfAuthors()
                + separator
                + classInfo.getNumberOfRevisions()
                + separator
                + classInfo.getNumberDefectsFixed()
                + separator
                + (classInfo.isBuggy() ? "True" : "False");
    }
}