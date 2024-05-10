package main;

import computer.BuggyClassesComputer;
import computer.MetricsComputer;
import computer.TicketFilter;
import computer.VersionsFixer;
import model.TicketInfo;
import model.VersionInfo;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import retriever.ClassesRetriever;
import retriever.CommitRetriever;
import retriever.TicketRetriever;
import retriever.VersionRetriever;
import writer.ARFWriter;
import writer.CSVWriter;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class ExecutionFlow {

    private ExecutionFlow() {
    }

    public static void execute(String projectPath, String projectName) throws IOException, URISyntaxException, GitAPIException {
        FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();

        Repository repo = repositoryBuilder.setGitDir(new File(projectPath + projectName + "/.git")).build();
        Git git = new Git(repo);

        VersionRetriever versionRetriever = new VersionRetriever(projectName);
        List<VersionInfo> versionInfoList = versionRetriever.retrieveVersions();

        VersionInfo firstVersion = versionInfoList.get(0);
        VersionInfo lastVersion = versionInfoList.get(versionInfoList.size() - 1);

        CommitRetriever commitRetriever = new CommitRetriever(projectName, git, lastVersion.getVersionDate());
        commitRetriever.retrieveCommitListForAllVersions(versionInfoList);

        TicketRetriever ticketRetriever = new TicketRetriever(projectName);
        List<TicketInfo> ticketInfoList = ticketRetriever.retrieveBugTicket(versionInfoList);

        TicketFilter filter = new TicketFilter(projectName);
        List<TicketInfo> filteredList = filter.filterTicketByVersions(ticketInfoList, firstVersion.getVersionDate());

        VersionsFixer versionsFixer = new VersionsFixer();
        versionsFixer.fixInjectedAndAffectedVersions(filteredList, versionInfoList);

        ClassesRetriever classesRetriever = new ClassesRetriever(projectName, repo);
        classesRetriever.retrieveClassesForAllVersions(versionInfoList);

        List<TicketInfo> completeTicketList = commitRetriever.retrieveFixCommitListForAllTickets(filteredList, firstVersion, lastVersion);

        MetricsComputer metricsComputer = new MetricsComputer(projectName, repo, git);
        metricsComputer.computeMetrics(versionInfoList, completeTicketList);

        buildTrainingSets(projectName, versionInfoList, completeTicketList, repo, git);
        buildTestingSets(projectName, versionInfoList, completeTicketList, repo, git);

        git.close();
        repo.close();

    }

    private static void buildTestingSets(String projectName, List<VersionInfo> versionInfoList, List<TicketInfo> ticketInfoList, Repository repo, Git git) throws IOException, GitAPIException {
        CSVWriter csvWriter = new CSVWriter(projectName);
        ARFWriter arfWriter = new ARFWriter(projectName);

        BuggyClassesComputer buggyClassesComputer = new BuggyClassesComputer(projectName, repo, git);
        buggyClassesComputer.computeBuggyClassesForAllVersions(ticketInfoList, versionInfoList);

        for (int index = 0; index < versionInfoList.size() / 2; index++) {
            csvWriter.writeInfoAsCSV(List.of(versionInfoList.get(index + 1)), index, false);
            arfWriter.writeInfoAsARF(List.of(versionInfoList.get(index + 1)), index, false);
        }

    }

    private static void buildTrainingSets(String projectName, List<VersionInfo> versionInfoList, List<TicketInfo> ticketInfoList, Repository repo, Git git) throws IOException, GitAPIException {
        CSVWriter csvWriter = new CSVWriter(projectName) ;
        ARFWriter arfWriter = new ARFWriter(projectName) ;
        BuggyClassesComputer buggyClassesComputer = new BuggyClassesComputer(projectName, repo, git) ;

        for (int index = 0 ; index < versionInfoList.size() / 2 ; index++) {
            List<VersionInfo> trainingVersionList = versionInfoList.subList(0, index + 1) ;
            VersionInfo lastVersion = versionInfoList.get(index) ;

            List<TicketInfo> trainingTicketList = new ArrayList<>(ticketInfoList) ;
            trainingTicketList.removeIf(ticketInfo -> ticketInfo.getResolutionDate().isAfter(lastVersion.getVersionDate())) ;

            buggyClassesComputer.computeBuggyClassesForAllVersions(trainingTicketList, trainingVersionList);

            csvWriter.writeInfoAsCSV(trainingVersionList, index, true);
            arfWriter.writeInfoAsARF(trainingVersionList, index, true);
        }
    }
}