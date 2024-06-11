package utils;

import model.retrieve.ClassInfo;
import model.retrieve.TicketInfo;
import model.retrieve.VersionInfo;
import org.eclipse.jgit.revwalk.RevCommit;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class LogWriter {

    private LogWriter() {}

    private static final String SEPARATOR = "----------------------------------------------------------------------------------" ;

    public static void writeVersionLog(String projectName, List<VersionInfo> versionInfoList, String phase) throws IOException {
        Files.createDirectories(PathBuilder.buildLogPath(projectName));

        try (BufferedWriter writer = Files.newBufferedWriter(
                Path.of(PathBuilder.buildLogPath(projectName).toString(), phase))) {

            writer.write("Versioni Totali >> " + versionInfoList.size() + "\n\n");

            Integer commitNumber = 0;
            StringBuilder stringBuilder = new StringBuilder();

            for (VersionInfo versionInfo : versionInfoList) {
                stringBuilder.append("Number >> ").append(versionInfo.getReleaseNumber()).append("\n");
                stringBuilder.append("VersionName >> ").append(versionInfo.getVersionName()).append("\n");
                stringBuilder.append("VersionDate >> ").append(versionInfo.getVersionDate()).append("\n");
                stringBuilder.append("ClassList Size >> ").append(versionInfo.getClassInfoList().size()).append("\n");
                stringBuilder.append("CommitList Size >> ").append(versionInfo.getVersionCommitList().size()).append("\n\n");
                commitNumber += versionInfo.getVersionCommitList().size();
                stringBuilder.append(SEPARATOR).append("\n\n");
            }
            stringBuilder.append("Commit Totali >> ").append(commitNumber).append("\n\n");
            stringBuilder.append(SEPARATOR).append("\n\n");
            writer.write(stringBuilder.toString());
        }
    }




    public static void writeTicketLog(String projectName, List<TicketInfo> ticketInfoList, String phase) throws IOException {
        Files.createDirectories(PathBuilder.buildLogPath(projectName));

        try (BufferedWriter writer = Files.newBufferedWriter(
                Path.of(PathBuilder.buildLogPath(projectName).toString(), phase))) {

            writer.write("Ticket Totali >> " + ticketInfoList.size() + "\n\n");

            for (TicketInfo ticketInfo : ticketInfoList) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Ticket ID >> ").append(ticketInfo.getTicketId()).append("\n");
                stringBuilder.append("Opening Date >> ").append(ticketInfo.getCreateDate()).append("\n");
                stringBuilder.append("Resolution Date >> ").append(ticketInfo.getResolutionDate()).append("\n");
                stringBuilder.append("Injected Version >> ").append(ticketInfo.getInjectedVersion() == null ? "NULL" : ticketInfo.getInjectedVersion().getVersionName()).append("\n");
                stringBuilder.append("Fix Version >> ").append(ticketInfo.getFixVersion().getVersionName()).append("\n");
                stringBuilder.append("Opening Version >> ").append(ticketInfo.getOpeningVersion().getVersionName()).append("\n");

                stringBuilder.append("Affected Versions >> ").append("[");
                for (int i = 0; i < ticketInfo.getAffectedVersionList().size(); i++) {
                    stringBuilder.append(ticketInfo.getAffectedVersionList().get(i).getVersionName());
                    if (i != ticketInfo.getAffectedVersionList().size() - 1) {
                        stringBuilder.append(", ");
                    }
                }
                stringBuilder.append("]").append("\n");

                stringBuilder.append("Fix Commit List >> ").append("\n");
                for (RevCommit commit : ticketInfo.getFixCommitList()) {
                    stringBuilder.append("\t").append(commit.getName()).append("\n");
                }

                stringBuilder.append(SEPARATOR).append("\n\n");
                writer.write(stringBuilder.toString());
            }
        }
    }


    public static void writeProportionLog(String projectName, List<TicketInfo> ticketInfoList, List<Float> proportionValues, float coldStartValue, List<Float> coldStartArray) throws IOException {
        Files.createDirectories(PathBuilder.buildLogPath(projectName));

        try (BufferedWriter writer = Files.newBufferedWriter(
                Path.of(PathBuilder.buildLogPath(projectName).toString(), "Proportion"))) {

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Cold Start Array >> ").append(coldStartArray).append("\n");
            stringBuilder.append("Cold Start Value >> ").append(coldStartValue).append("\n\n");

            stringBuilder.append(SEPARATOR).append("\n\n");

            for (int i = 0; i < ticketInfoList.size(); i++) {
                TicketInfo ticketInfo = ticketInfoList.get(i);
                stringBuilder.append("Ticket ID >> ").append(ticketInfo.getTicketId()).append("\n");
                stringBuilder.append("Proportion Value >> ").append(proportionValues.get(i) == null ? "Null" : proportionValues.get(i)).append("\n");

                stringBuilder.append(SEPARATOR).append("\n\n");
            }

            writer.write(stringBuilder.toString());
        }
    }
    public static void writeBuggyClassesLog(String projectName, List<VersionInfo> versionInfoList) throws IOException {
        Files.createDirectories(PathBuilder.buildLogPath(projectName));

        try (BufferedWriter writer = Files.newBufferedWriter(
                Path.of(PathBuilder.buildLogPath(projectName).toString(), "BuggyClasses"))) {

            StringBuilder stringBuilder = new StringBuilder();
            for (VersionInfo versionInfo : versionInfoList) {
                stringBuilder.append("Version Name >> ").append(versionInfo.getVersionName()).append("\n");
                int buggyClassesNumber = 0;
                for (ClassInfo classInfo : versionInfo.getClassInfoList()) {
                    if (classInfo.isBuggy()) {
                        buggyClassesNumber++;
                    }
                }
                stringBuilder.append("Numero Buggy >> ").append(buggyClassesNumber).append("\n");
                stringBuilder.append(SEPARATOR).append("\n\n");
            }

            writer.write(stringBuilder.toString());
        }
    }


    public static void logRuntimeException(String projectName) {
        try {
            Files.createDirectories(PathBuilder.buildLogPath(projectName));

            try (BufferedWriter writer = Files.newBufferedWriter(
                    Path.of(PathBuilder.buildLogPath(projectName).toString(), "ErrorLog"),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {

                writer.write("RunTime Exception\n");
                writer.write(SEPARATOR + "\n");
            }
        } catch (IOException e) {
            System.err.println("Errore durante la scrittura nel file di log: " + e.getMessage());
            e.printStackTrace();
        }
    }

}