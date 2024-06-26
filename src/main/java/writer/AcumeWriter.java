package writer;

import model.retrieve.Acume;
import model.weka.WekaClassifier;
import utils.LogWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class AcumeWriter {

    private AcumeWriter(){}
   public static void createANewFileAcume(String projName, WekaClassifier wekaClassifier, int fileIndex, List<Acume> acumeList) {
       try {
           File file = new File("output/acume/" + projName);
           if (!file.exists()) {
               boolean success = file.mkdirs();
               if (!success) {
                   throw new IOException();
               }
           }
           StringBuilder fileName = new StringBuilder();
           fileName.append("/").append(projName).append("_").append(wekaClassifier.getClassifierName()).append(wekaClassifier.getFilterName()).append(wekaClassifier.getSamplingType()).append(wekaClassifier.getIsCostSensitive()).append(fileIndex).append(".csv");
           file = new File("output/acume/" + projName + fileName);
           writeAcumeFile(file, acumeList);
       } catch (IOException | RuntimeException e) {
           LogWriter.logRuntimeException("ERRORE INASPETTATO IN AcumeWriter()");
       }
   }

   private static void writeAcumeFile(File file, List<Acume> acumeList){
       try (FileWriter fileWriter = new FileWriter(file)) {
           fileWriter.append("ID," +
                   "SIZE," +
                   "PREDICTED," +
                   "ACTUAL\n");
           for (Acume acumeFile : acumeList) {

               fileWriter.write(acumeFile.getIndex() + ",");                          //INDEX OF CLASS
               fileWriter.write(acumeFile.getSize() + ",");                           //SIZE OF CLASS
               fileWriter.write(acumeFile.getProbabilityOfBuggyness() + ",");         //PROBABILITY OF BUGGY
               fileWriter.write(acumeFile.isBuggy() + "\n");
           }

       } catch (IOException e) {
           LogWriter.logRuntimeException("ERRORE INASPETTATO IN writeAcumeFile()");
       }
   }

}
