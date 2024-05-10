package main;

import computer.BuggyClassesComputer;
import computer.MetricsComputer;
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

public class RetrievingFlow {

    private RetrievingFlow() {}

    public static int retrieve(String projectPath, String projectName) throws URISyntaxException, IOException, GitAPIException {
        FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
        Repository repo = repositoryBuilder.setGitDir(new File(Path.of(projectPath, projectName, ".git").toString())).build();
        Git git = new Git(repo) ;

        VersionRetriever versionRetriever = new VersionRetriever(projectName) ;
        List<VersionInfo> versionInfoList = versionRetriever.retrieveVersions() ;

        VersionInfo firstVersion = versionInfoList.get(0) ;
        VersionInfo lastVersion = versionInfoList.get(versionInfoList.size() - 1) ;

        CommitRetriever commitRetriever = new CommitRetriever(projectName, git, lastVersion.getVersionDate()) ;
        commitRetriever.retrieveCommitListForAllVersions(versionInfoList) ;

        TicketRetriever ticketRetriever = new TicketRetriever(projectName) ;
        List<TicketInfo> ticketInfoList = ticketRetriever.retrieveBugTicket(versionInfoList) ;

        VersionsFixer versionsFixer = new VersionsFixer() ;
        versionsFixer.fixInjectedAndAffectedVersions(ticketInfoList, versionInfoList);

        ClassesRetriever classesRetriever = new ClassesRetriever(projectName, repo) ;
        classesRetriever.retrieveClassesForAllVersions(versionInfoList);

        commitRetriever.retrieveFixCommitListForAllTickets(ticketInfoList, firstVersion, lastVersion) ;

        MetricsComputer metricsComputer = new MetricsComputer(projectName, repo, git) ;
        metricsComputer.computeMetrics(versionInfoList, ticketInfoList);

        buildTrainingSets(projectName, versionInfoList, ticketInfoList, repo, git) ;
        buildTestingSets(projectName, versionInfoList, ticketInfoList, repo, git) ;

        git.close();
        repo.close();

        return versionInfoList.size() / 2 ;
    }

    private static void buildTestingSets(String projectName, List<VersionInfo> versionInfoList, List<TicketInfo> ticketInfoList, Repository repo, Git git) throws IOException, GitAPIException {
        CSVWriter csvWriter = new CSVWriter(projectName) ;
        ARFWriter arfWriter = new ARFWriter(projectName) ;

        BuggyClassesComputer buggyClassesComputer = new BuggyClassesComputer(projectName, repo, git) ;
        buggyClassesComputer.computeBuggyClassesForAllVersions(ticketInfoList, versionInfoList);

        for (int index = 0 ; index < versionInfoList.size() / 2 ; index++) {
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