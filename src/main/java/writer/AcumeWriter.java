package writer;

import com.sun.istack.NotNull;
import enums.Classifiers;
import exceptions.ImpossibleDirectoryCreationException;
import model.retrieve.Acume;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class AcumeWriter {

/*     AcumeWriter(){}
    public static void writeCsvForAcume(String projectName, Classifiers classifierEnum, List<Acume> acume) throws IOException, ImpossibleDirectoryCreationException {

        String fileName = projectName.toUpperCase() + "_" + classifierEnum.toString().toUpperCase() + "_" + featureSelectionEnum.toString().toUpperCase() + "_" + samplingEnum.toString().toUpperCase() + "_" + costSensitiveEnum.toString().toUpperCase() + "_" + index.toString();
        File file = createANewFileAcume(projectName, fileName, FilenamesEnum.ACUME, index);

        writeOnCsvAcume(file, acume);

    }
   private static @NotNull File createANewFileAcume(String projectName, String fileName, FilenamesEnum fileEnum, int fileIndex) throws IOException, ImpossibleDirectoryCreationException {
        String enumFilename = FileUtils.enumToFilename(fileEnum, fileIndex);
        Path dirPath = Path.of("retrieved_data/" + projectName + "/acume/");
        return getFile(fileName, ".csv", enumFilename, dirPath, true);
    }*/

    /*private static @NotNull File getFile(String projName, String endPath, String enumFilename, Path dirPath, boolean acume) throws IOException, ImpossibleDirectoryCreationException {

        Path pathname;

        if(!acume) {
            pathname = Path.of(dirPath.toString(), projName + enumFilename + endPath);
        }else{
            projName = projName + ".csv";
            pathname = Path.of(dirPath.toString(), projName);
        }

        return getFile(dirPath, pathname);
    }

    private static void writeOnCsvAcume(File file, List<Acume> acumeList) throws IOException {

        try(FileWriter fileWriter = new FileWriter(file)) {

            fileWriter.write("ID," + "Size," + "Predicted," + "Actual");

            fileWriter.write("\n");

            for(Acume acume : acumeList) {

                fileWriter.write(acume.getIndex() + ",");                          //INDEX OF CLASS
                fileWriter.write(acume.getSize() + ",");                           //SIZE OF CLASS
                fileWriter.write(acume.getProbabilityOfBuggyness() + ",");         //PROBABILITY OF BUGGY
                fileWriter.write(acume.isBuggy());

                fileWriter.write("\n");

            }

        }

    }*/

/*    private static @NotNull File createANewFile(String projName, FilenamesEnum fileEnum, int fileIndex, String endPath) throws IOException, ImpossibleDirectoryCreationException{
        String enumFilename = FileUtils.enumToFilename(fileEnum, fileIndex);
        Path dirPath = Path.of("retrieved_data/", projName, FileUtils.enumToDirectoryName(fileEnum));

        Path pathname = Path.of(dirPath.toString(), projName + enumFilename + endPath);

        return getFile(dirPath, pathname);
    }

    private static @NotNull File getFile(Path dirPath, Path pathname) throws ImpossibleDirectoryCreationException, IOException{

        File dir = new File(dirPath.toUri());
        File file = new File(pathname.toUri());

        if(!dir.exists() && !file.mkdirs()) {
            throw new ImpossibleDirectoryCreationException();               //Exception: dir creation impossible
        }

        deleteFile(file);

        return file;

    }*/
}
