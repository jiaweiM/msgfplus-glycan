package omics.msgf.msutil;

import java.io.File;

/**
 * class for IO files of Database search.
 */
public class DBSearchIOFiles
{
    private File specFile;
    private SpecFileFormat specFileFormat;
    private File outputFile;

    /**
     * Constructor
     *
     * @param specFile       spectrum file
     * @param specFileFormat spectrum file format
     * @param outputFile     output file
     */
    public DBSearchIOFiles(File specFile, SpecFileFormat specFileFormat, File outputFile)
    {
        this.specFile = specFile;
        this.specFileFormat = specFileFormat;
        this.outputFile = outputFile;
    }

    public File getSpecFile()
    {
        return specFile;
    }

    public SpecFileFormat getSpecFileFormat()
    {
        return specFileFormat;
    }

    public File getOutputFile()
    {
        return outputFile;
    }
}
