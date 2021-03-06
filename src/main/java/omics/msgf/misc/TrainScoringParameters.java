package omics.msgf.misc;

import omics.msgf.msscorer.NewRankScorer;
import omics.msgf.msscorer.NewScorerFactory;
import omics.msgf.msscorer.ScoringParameterGeneratorWithErrors;
import omics.msgf.msutil.*;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Calendar;

public class TrainScoringParameters
{

    private static final String PARAM_DIR = System.getProperty("user.home") + "/Research/Data/TrainingMSGFPlus/new";
    //	private static final String PARAM_DIR = System.getProperty("user.home")+"/Developments/MS_Java_Dev/src/main/resources/ionstat";
    private static final String BACKUP_DIR = System.getProperty("user.home") + "/Research/Data/TrainingMSGFPlus/backup";
    private static final String SPEC_DIR = System.getProperty("user.home") + "/Research/Data/TrainingMSGFPlus/AnnotatedSpectra";

    public static void main(String argv[]) throws Exception
    {
//		backup();
//		createParamFiles();
        testParamFiles();
    }

    public static void backup() throws Exception
    {
        File paramDir = new File(PARAM_DIR);
        boolean paramExists = false;
        for (File paramFile : paramDir.listFiles()) {
            if (paramFile.getName().endsWith(".param"))
                paramExists = true;
        }
        if (!paramExists) {
            System.out.println("No param file to backup.");
            return;
        }
        Calendar calendar = Calendar.getInstance();
        String dateStr = calendar.get(Calendar.MONTH) + "_" + calendar.get(Calendar.DAY_OF_MONTH) + "_" + calendar.get(Calendar.YEAR);
        String backupDirName = "ParamBackup_" + dateStr;
        File backupDir = new File(BACKUP_DIR + "/" + backupDirName);
        if (backupDir.exists()) {
            System.out.println("Backup directory already exists: " + backupDir.getPath());
            System.exit(-1);
        }
        backupDir.mkdir();
        System.out.println(backupDir.getPath() + " is created.");

        boolean backupSuccess = true;
        for (File paramFile : paramDir.listFiles()) {
            if (paramFile.getName().endsWith(".param")) {
                File newFile = new File(backupDir, paramFile.getName());
                boolean isBackupSuccessful = paramFile.renameTo(newFile);
                System.out.println("Moving " + paramFile.getPath() + " to " + newFile.getPath() + (isBackupSuccessful ? " succeeded." : " failed."));
                if (!isBackupSuccessful) {
                    backupSuccess = false;
                    break;
                }
            }
        }
        if (backupSuccess)
            System.out.println("Backup complete.");
        else {
            backupDir.delete();
            System.out.println(backupDir.getPath() + " is deleted.");
            System.out.println("Backup failed.");
            System.exit(0);
        }
    }

    public static void createParamFiles() throws Exception
    {
        File specDir = new File(SPEC_DIR);
        if (!specDir.exists()) {
            System.err.println("Training spectra directory doesn't exist:" + specDir.getPath());
            System.exit(-1);
        }

        AminoAcidSet aaSet = AminoAcidSet.getStandardAminoAcidSetWithFixedCarbamidomethylatedCys();
        for (File specFile : specDir.listFiles()) {
            String specFileName = specFile.getName();
            if (specFileName.endsWith(".mgf")) {
//				if(!specFileName.equals("ETD_LowRes_LysC.mgf"))
//					continue;

                String id = specFileName.substring(0, specFileName.lastIndexOf('.'));
                String[] token = id.split("_");
                if (token.length != 3 && token.length != 4) {
                    System.err.println("Wrong file name: " + specFile.getName());
                    System.exit(-1);
                }
                String actMethodStr = token[0];
                String instTypeStr = token[1];
                String enzymeStr = token[2];
                String protocolStr = null;
                if (token.length == 4)
                    protocolStr = token[3];

                ActivationMethod actMethod = ActivationMethod.get(actMethodStr);
                if (actMethod == null) {
                    System.err.println("Unrecognized ActivationMethod: " + actMethodStr + "(" + specFileName + ")");
                    System.exit(-1);
                }
                InstrumentType instType = InstrumentType.get(instTypeStr);
                if (instType == null) {
                    System.err.println("Unrecognized InstrumentType: " + instTypeStr + "(" + specFileName + ")");
                    System.exit(-1);
                }
                Enzyme enzyme = Enzyme.getEnzymeByName(enzymeStr);
                if (enzyme == null) {
                    System.err.println("Unrecognized Enzyme: " + enzymeStr + "(" + specFileName + ")");
                    System.exit(-1);
                }

                Protocol protocol = null;
                if (protocolStr != null) {
                    protocol = Protocol.get(protocolStr);
                    if (protocol == null) {
                        System.err.println("Unrecognized Protocol: " + protocolStr + "(" + specFileName + ")");
                        System.exit(-1);
                    }
                } else
                    protocol = Protocol.AUTOMATIC;

                if (actMethod == null || instType == null || enzyme == null || protocol == null) {
                    System.err.println("Wrong file name: " + specFile.getName());
                    System.exit(-1);
                }

                NewScorerFactory.SpecDataType dataType = new NewScorerFactory.SpecDataType(actMethod, instType, enzyme, protocol);
                System.out.println("Processing " + dataType.toString());
                ScoringParameterGeneratorWithErrors.generateParameters(
                        specFile,
                        dataType,
                        aaSet,
                        new File(PARAM_DIR),
                        false,
                        false,
                        false);
            }
        }
        System.out.println("Successfully generated parameters!");
    }

    public static void testParamFiles() throws Exception
    {
        for (File f : new File(PARAM_DIR).listFiles()) {
            if (f.getName().endsWith(".param")) {
                System.out.println("Reading " + f.getName());
                InputStream is = new BufferedInputStream(new FileInputStream(f));
                NewRankScorer scorer = new NewRankScorer(new BufferedInputStream(is));
                System.out.println(scorer.getSpecDataType());
                if (!f.getName().substring(0, f.getName().lastIndexOf('.')).equals(scorer.getSpecDataType().toString())) {
                    System.out.println(f.getName().substring(0, f.getName().lastIndexOf('.')) + " != " + scorer.getSpecDataType().toString());
                    System.out.println("********* Mismatch **********");
                }
            }
        }
        System.out.println("Read Success");
    }
}
