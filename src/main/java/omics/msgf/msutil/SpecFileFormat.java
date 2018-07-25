package omics.msgf.msutil;

import java.util.ArrayList;


public class SpecFileFormat extends FileFormat
{
    public static final SpecFileFormat MGF;
    public static final SpecFileFormat MZXML;
    public static final SpecFileFormat MZML;
    public static final SpecFileFormat MS2;
    public static final SpecFileFormat PKL;
    public static final SpecFileFormat MZDATA;
    public static final SpecFileFormat DTA_TXT;

    private static ArrayList<SpecFileFormat> specFileFormatList;

    static {
        MGF = new SpecFileFormat(".mgf", "MS:1001062", "Mascot MGF file");
        MZXML = new SpecFileFormat(".mzXML", "MS:1000566", "ISB mzXML file");
        MZML = new SpecFileFormat(".mzML", "MS:1000584", "mzML file");
        MS2 = new SpecFileFormat(".ms2", "MS:1001466", "MS2 file");
        PKL = new SpecFileFormat(".pkl", "MS:1000565", "Micromass PKL file");
        MZDATA = new SpecFileFormat(".mzData", "MS:1000564", "PSI mzData file");
        DTA_TXT = new SpecFileFormat("_dta.txt", "MS:XXXXXXX", "PNNL dta.txt file");

        specFileFormatList = new ArrayList<>();
        specFileFormatList.add(MGF);
        specFileFormatList.add(MZXML);
        specFileFormatList.add(MZML);
        specFileFormatList.add(MS2);
        specFileFormatList.add(PKL);
        specFileFormatList.add(MZDATA);
        specFileFormatList.add(DTA_TXT);
    }

    private final String psiAccession;
    private final String psiName;

    /**
     * Constructor.
     *
     * @param suffix       file suffix
     * @param psiAccession psi accession
     * @param psiName      file format name
     */
    private SpecFileFormat(String suffix, String psiAccession, String psiName)
    {
        super(suffix);
        this.psiAccession = psiAccession;
        this.psiName = psiName;
    }

    /**
     * Return the {@link SpecFileFormat} of given file suffix
     *
     * @param specFileName file name suffix
     * @return {@link SpecFileFormat} of the suffix
     */
    public static SpecFileFormat getSpecFileFormat(String specFileName)
    {
        String lowerCaseFileName = specFileName.toLowerCase();
        for (SpecFileFormat f : specFileFormatList) {
            for (String suffix : f.getSuffixes()) {
                if (lowerCaseFileName.endsWith(suffix.toLowerCase()))
                    return f;
            }
        }
        return null;
    }

    public String getPSIAccession()
    {
        return psiAccession;
    }

    public String getPSIName()
    {
        return psiName;
    }
}
