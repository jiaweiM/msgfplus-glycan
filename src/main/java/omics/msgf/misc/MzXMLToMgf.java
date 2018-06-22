package omics.msgf.misc;

import omics.msgf.msutil.ActivationMethod;
import omics.msgf.msutil.Spectrum;
import omics.msgf.parser.MzXMLSpectraIterator;

import java.io.*;


public class MzXMLToMgf
{
    public static void main(String argv[])
    {
        File mzXMLFile = null;
        File mgfFile = null;
        ActivationMethod activationMethod = null;

        if (argv.length != 2 && argv.length != 3)
            printUsageAndExit("Invalid parameters");

        mzXMLFile = new File(argv[0]);
        if (!mzXMLFile.exists())
            printUsageAndExit(argv[0] + " doesn't exist!");
        String ext;
        if (!mzXMLFile.isDirectory()) {
            ext = mzXMLFile.getName().substring(mzXMLFile.getName().lastIndexOf('.') + 1);
            if (!ext.equalsIgnoreCase("mzxml") && !ext.equalsIgnoreCase("mzml"))
                printUsageAndExit(argv[0] + " must be *.mzXML!");
        }

        mgfFile = new File(argv[1]);
        ext = mgfFile.getName().substring(mgfFile.getName().lastIndexOf('.') + 1);
        if (!ext.equalsIgnoreCase("mgf"))
            printUsageAndExit(argv[1] + " must be *.mgf!");

        if (argv.length == 3) {
            activationMethod = ActivationMethod.get(argv[2]);
            if (activationMethod == null)
                printUsageAndExit("Cannot recognize activaion method " + argv[2]);
        }
        convert(mzXMLFile, mgfFile, activationMethod);
    }

    public static void printUsageAndExit(String message)
    {
        System.err.println(message);
        System.out.println("usage: MzXMLToMgf mzXML(*.mzXML or directory) mgfFile(*.mgf) [activationMethod]");
        System.exit(-1);
    }

    public static void convert(File mzXMLFile, File mgfFile, ActivationMethod activationMethod)
    {

        PrintStream out = null;
        try {
            out = new PrintStream(new BufferedOutputStream(new FileOutputStream(mgfFile)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        File[] mzXMLFileList;
        if (mzXMLFile.isDirectory())
            mzXMLFileList = mzXMLFile.listFiles(new FileFilter.FileExtFilter("mzXML"));
        else {
            mzXMLFileList = new File[1];
            mzXMLFileList[0] = mzXMLFile;
        }

        int numSpecs = 0;

        for (File f : mzXMLFileList) {
            MzXMLSpectraIterator itr = new MzXMLSpectraIterator(f.getPath());
            while (itr.hasNext()) {
                Spectrum spec = itr.next();
                spec.setTitle(f.getName() + ":" + spec.getScanNum());
                if (spec.getActivationMethod() != null && activationMethod != null) {
                    if (spec.getActivationMethod() != activationMethod)
                        continue;
                }
                if (numSpecs > 0)
                    out.println();
                spec.outputMgf(out);
                numSpecs++;

            }
        }

        if (out != null)
            out.close();
        System.out.println(numSpecs + " converted.");
    }
}
