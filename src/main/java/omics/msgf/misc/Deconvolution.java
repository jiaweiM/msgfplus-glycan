package omics.msgf.misc;

import omics.msgf.msutil.SpectraIterator;
import omics.msgf.msutil.Spectrum;
import omics.msgf.parser.MgfSpectrumParser;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class Deconvolution
{
    public static void main(String argv[]) throws Exception
    {
        if (argv.length < 2 || argv.length % 2 != 0)
            printUsageAndExit("The number of parameters must be even.");

        File specFile = null;
        File deconvSpecFile = null;
        for (int i = 0; i < argv.length; i += 2) {
            if (!argv[i].startsWith("-") || i + 1 >= argv.length)
                printUsageAndExit("Invalid parameters");
            else if (argv[i].equalsIgnoreCase("-i")) {
                specFile = new File(argv[i + 1]);
                if (!specFile.exists())
                    printUsageAndExit(argv[i + 1] + " doesn't exist!");
                if (!specFile.getName().endsWith(".mgf")) {
                    printUsageAndExit(argv[i + 1] + " is not mgf spectrum!");
                }
            } else if (argv[i].equalsIgnoreCase("-o")) {
                deconvSpecFile = new File(argv[i + 1]);
                if (deconvSpecFile.exists())
                    printUsageAndExit(argv[i + 1] + " already exists!");
                if (!deconvSpecFile.getName().endsWith(".mgf")) {
                    printUsageAndExit(argv[i + 1] + " is not mgf spectrum!");
                }
            }
        }

        deconvolute(specFile, deconvSpecFile);
    }

    public static void printUsageAndExit(String message)
    {
        System.out.println(message);
        System.out.println("Usage: java Deconvolution -i SpecFileName(*.mgf) -o DeconvolutedSpecFileName(*.mgf)");
        System.exit(-1);
    }

    public static void deconvolute(File specFile, File deconvSpecFile) throws Exception
    {
        PrintStream out = new PrintStream(new BufferedOutputStream(new FileOutputStream(deconvSpecFile)));

        int numSpecs = 0;
        SpectraIterator itr = new SpectraIterator(specFile.getPath(), new MgfSpectrumParser());
        while (itr.hasNext()) {
            Spectrum spec = itr.next();
            spec.getDeconvolutedSpectrum(0.02f).outputMgf(out);
            numSpecs++;
        }
        out.close();
        System.out.println(numSpecs + " are deconvoluted.");
    }
}	
