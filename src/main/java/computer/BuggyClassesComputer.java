package computer;

import model.retrieve.ClassInfo;
import model.retrieve.TicketInfo;
import model.retrieve.VersionInfo;
import org.eclipse.jgit.api.DiffCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BuggyClassesComputer {

    private final Repository repo ;
    private final Git git ;

    private final String projectName ;

    public BuggyClassesComputer(String projectName, Repository repo, Git git) {
        this.repo = repo ;
        this.git = git ;
        this.projectName = projectName ;
    }

    public void computeBuggyClassesForAllVersions(List<TicketInfo> ticketInfoList, List<VersionInfo> versionInfoList) throws GitAPIException, IOException {
        Logger.getGlobal().log(Level.INFO, "{0}", "Calcolo Classi Buggy per " + projectName.toUpperCase());
        for (TicketInfo ticketInfo : ticketInfoList) {
            List<String> buggyClasses = computeBuggyClassesByTicket(ticketInfo);

            List<VersionInfo> affectedVersionList = ticketInfo.getAffectedVersionList() ;
            setBuggyToClasses(buggyClasses, affectedVersionList) ;
        }

        StringBuilder stringBuilder = new StringBuilder() ;
        stringBuilder.append("\n").append("Numero Classi Buggy Per Versione").append("\n") ;
        for (VersionInfo versionInfo : versionInfoList) {
            Integer buggyClassesNumber = 0 ;
            for (ClassInfo classInfo : versionInfo.getClassInfoList()) {
                if (classInfo.isBuggy()) {
                    buggyClassesNumber ++ ;
                }
            }
            stringBuilder.append(versionInfo.getVersionName()).append(" >> ").append(buggyClassesNumber).append("\n") ;
        }
        Logger.getGlobal().log(Level.INFO, "{0}", stringBuilder);
    }
    //rimuovere metodo e unirlo con quello di MetricsComputer ??
    private List<String> computeBuggyClassesByTicket(TicketInfo ticketInfo) throws IOException, GitAPIException {
        List<String> buggyClasses = new ArrayList<>() ;

        List<RevCommit> fixCommitList = ticketInfo.getFixCommitList() ;
        for (RevCommit commit : fixCommitList) {
            List<String> changedClassesForCommit = computeChangedClassesByCommit(commit) ;
            buggyClasses.addAll(changedClassesForCommit) ;
        }

        return buggyClasses ;
    }

    private List<String> computeChangedClassesByCommit(RevCommit commit) throws IOException, GitAPIException {
        List<String> buggyClassesForCommit = new ArrayList<>() ;

        ObjectReader reader = repo.newObjectReader() ;
        RevCommit prevCommit = commit.getParent(0) ;

        CanonicalTreeParser commitTreeParser = new CanonicalTreeParser() ;
        commitTreeParser.reset(reader, commit.getTree());
        CanonicalTreeParser prevCommitTreeParser = new CanonicalTreeParser() ;
        prevCommitTreeParser.reset(reader, prevCommit.getTree());

        DiffCommand diffCommand = git.diff() ;
        List<DiffEntry> diffEntryList = diffCommand
                .setShowNameOnly(true)
                .setNewTree(commitTreeParser)
                .setOldTree(prevCommitTreeParser)
                .call() ;

        for (DiffEntry diffEntry : diffEntryList) {
            if (diffEntry.getChangeType().equals(DiffEntry.ChangeType.MODIFY) && diffEntry.getNewPath().endsWith(".java") && !diffEntry.getNewPath().contains("/test/")) {
                buggyClassesForCommit.add(diffEntry.getNewPath());
            }
        }

        return buggyClassesForCommit ;
    }

    private void setBuggyToClasses(List<String> buggyClasses, List<VersionInfo> affectedVersionList) {

        for (VersionInfo versionInfo : affectedVersionList) {
            List<ClassInfo> classInfoList = versionInfo.getClassInfoList() ;
            for (ClassInfo classInfo : classInfoList) {
                if (classIsBuggy(classInfo.getName(), buggyClasses)) {
                    classInfo.setBuggy(true);
                }
            }
        }
    }

    private boolean classIsBuggy(String className, List<String> buggyClasses) {
        for (String buggyClass : buggyClasses) {
            if (buggyClass.compareTo(className) == 0) {
                return true ;
            }
        }
        return false ;
    }
}
