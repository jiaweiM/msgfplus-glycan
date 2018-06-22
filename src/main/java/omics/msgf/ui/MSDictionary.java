package omics.msgf.ui;

import omics.msgf.msdictionary.MSDicLauncher;
import omics.msgf.msgf.Tolerance;
import omics.msgf.msscorer.NewAdditiveScorer;
import omics.msgf.msscorer.NewRankScorer;
import omics.msgf.msscorer.NewScorerFactory;
import omics.msgf.msutil.*;
import omics.msgf.params.ParamParser;
import omics.msgf.parser.MgfSpectrumParser;
import omics.msgf.parser.MzXMLSpectraIterator;
import omics.msgf.parser.PklSpectrumParser;
import omics.msgf.parser.SpectrumParser;
import omics.msgf.suffixarray.SuffixArray;
import omics.msgf.suffixarray.SuffixArraySequence;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;

public class MSDictionary
{
    public static void main(String argv[])
    {
        if (argv.length != 2 && argv.length != 4) {
            printUsageAndExit();
            System.exit(-1);
        }
        String paramFileName = null;
        String outputFileName = null;
        for (int i = 0; i < argv.length; i += 2) {
            if (!argv[i].startsWith("-") || i + 1 >= argv.length)
                printUsageAndExit();
            if (argv[i].equalsIgnoreCase("-i")) {
                paramFileName = argv[i + 1];
            } else if (argv[i].equalsIgnoreCase("-o")) {
                outputFileName = argv[i + 1];
            }
        }
        if (paramFileName == null) {
            System.out.println("Error: parameter file is missing.");
            printUsageAndExit();
        }

        runMSDictionary(paramFileName, outputFileName);
    }

    public static void printUsageAndExit()
    {
        System.out.println("MS-Dictionary (v.20100201)\n" +
                "usage: java -jar MSDictionary.jar -i paramFile [-o outputFile]\n" +
                "(example: java -Xmx3000M -jar MSDictionary.jar -i sampleInput.txt -o test.txt)");
        System.exit(-1);
    }

    // default parameters
    public static void runMSDictionary(String paramFile, String outputFileName)
    {
        ParamParser.Parameters params = ParamParser.parseFromFile(paramFile);

        // Spectrum
        String specFileName = params.getParameter("Spectrum");
        if (!new File(specFileName).exists())
            printParsingErrorAndExit(specFileName + " doesn't exist.");
        Iterator<Spectrum> specIterator = null;
        String ext = specFileName.substring(specFileName.lastIndexOf('.') + 1);
        if (ext.equalsIgnoreCase("mzxml"))
            specIterator = new MzXMLSpectraIterator(specFileName);
        else {
            SpectrumParser parser = null;
            if (ext.equalsIgnoreCase("mgf"))
                parser = new MgfSpectrumParser();
            else if (ext.equalsIgnoreCase("pkl"))
                parser = new PklSpectrumParser();
            else
                printParsingErrorAndExit(ext + " format is not supported");

            try {
                specIterator = new SpectraIterator(specFileName, parser);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        // Database
        String dbFileName = params.getParameter("Database");
        SuffixArray sa = null;
        if (dbFileName != null)    // if no database is specified, just generate reconstructions
        {
            if (!new File(dbFileName).exists())
                printParsingErrorAndExit(dbFileName + " doesn't exist.");
            sa = new SuffixArray(new SuffixArraySequence(dbFileName, omics.msgf.sequences.Constants.AMINO_ACIDS_19));
        }

        // Scoring parameters
        String scoringParamFile = params.getParameter("ScoringParams");
        NewAdditiveScorer scorer;
        if (scoringParamFile == null)
            scorer = NewScorerFactory.get(ActivationMethod.CID, InstrumentType.LOW_RESOLUTION_LTQ, Enzyme.TRYPSIN, Protocol.AUTOMATIC);
        else
            scorer = new NewRankScorer(scoringParamFile);

        MSDicLauncher msDicLauncher = new MSDicLauncher(specIterator, scorer, sa);

        // Parent mass tolerance
        Tolerance pmTolerance = null;
        String pmTolStr = params.getParameter("PMTolerance");
        if (pmTolStr != null)
            pmTolerance = Tolerance.parseToleranceStr(pmTolStr);
        if (pmTolerance == null)
            printParsingErrorAndExit("Input file parsing error: invalid parent mass tolerance.");
        else
            msDicLauncher.pmTolerance(pmTolerance);

        // Fragment mass tolerance
//		Tolerance fragTolerance = null;
//		String fragTolStr = params.getParameter("Tolerance");
//		if(fragTolStr != null)
//			fragTolerance = Tolerance.parseToleranceStr(fragTolStr);
//		if(fragTolerance == null)
//			printParsingErrorAndExit("Input file parsing error: invalid fragment mass tolerance.");
//		else
//			msDicLauncher.fragTolerance(fragTolerance);

        // Spectral probability
        Float specProb = null;
        String specProbStr = params.getParameter("SpecProb");
        if (specProbStr != null)
            specProb = Float.parseFloat(specProbStr);
        if (specProb == null)
            printParsingErrorAndExit("Input file parsing error: invalid spectral probability.");
        else
            msDicLauncher.specProb(specProb);

        // Number of reconstructions
        Float numRecs = null;
        String numRecsStr = params.getParameter("NumRecs");
        if (numRecsStr != null)
            numRecs = Float.parseFloat(numRecsStr);
        if (numRecs == null)
            printParsingErrorAndExit("Input file parsing error: invalid spectral probability.");
        else
            msDicLauncher.numRecs(numRecs);

        Integer isNumInclusive = params.getIntParameter("IsNumInclusive");
        if (isNumInclusive == null)
            printParsingErrorAndExit("Input file parsing error: invalid IsNumInclusive field.");
        else if (isNumInclusive == 1)
            msDicLauncher.setNumInclusive();

        Integer isTrypticOnly = params.getIntParameter("IsTrypticOnly");
        if (isTrypticOnly == null)
            printParsingErrorAndExit("Input file parsing error: invalid IsTrypticOnly field.");
        else if (isTrypticOnly == 0)
            msDicLauncher.allowNonTryptic();

        Integer msgfThreshold = params.getIntParameter("MSGFThreshold");
        if (msgfThreshold == null)
            printParsingErrorAndExit("Input file parsing error: invalid MSGFThreshold field.");
        else
            msDicLauncher.msgfScoreThreshold(msgfThreshold);

        Float minParentMass = params.getFloatParameter("MinParentMass");
        if (minParentMass == null)
            printParsingErrorAndExit("Input file parsing error: invalid minimum parent mass.");
        else
            msDicLauncher.minParentMass(minParentMass);

        Float maxParentMass = params.getFloatParameter("MaxParentMass");
        if (maxParentMass == null)
            printParsingErrorAndExit("Input file parsing error: invalid maximum parent mass.");
        else
            msDicLauncher.maxParentMass(maxParentMass);

        if (outputFileName != null)
            msDicLauncher.outputFileName(outputFileName);

        msDicLauncher.runMSDictionary();
    }


    public static void printParsingErrorAndExit(String message)
    {
        System.err.println(message);
        System.exit(-1);
    }
}
