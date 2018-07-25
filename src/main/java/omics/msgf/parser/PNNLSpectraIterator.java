package omics.msgf.parser;

import omics.msgf.msutil.ScanType;
import omics.msgf.msutil.SpectraIterator;
import omics.msgf.msutil.Spectrum;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Iterator;

public class PNNLSpectraIterator extends SpectraIterator
{

    private HashMap<Integer, ScanType> scanNumScanTypeMap;

    public PNNLSpectraIterator(String fileName) throws FileNotFoundException
    {
        super(fileName, new PNNLSpectrumParser());
        scanNumScanTypeMap = PNNLSpectrumParser.getScanTypeMap(fileName);
    }

    public static void main(String argv[]) throws Exception
    {
        String fileName = System.getProperty("user.home") + "/Test/Matt/QC_Shew_11_03_200ng_4_23Aug11_Hawk_11-05-04p_dta.txt";
        PNNLSpectraIterator itr = new PNNLSpectraIterator(fileName);
        Iterator<Spectrum> specItr = itr.iterator();
        while (specItr.hasNext()) {
            Spectrum spec = specItr.next();
            System.out.println(spec.getScanNum() + "\t" + spec.getActivationMethod());
        }
    }

    @Override
    public Spectrum next()
    {
        if (scanNumScanTypeMap == null)
            return super.next();

        Spectrum spec = super.next();
        ScanType scanType = scanNumScanTypeMap.get(spec.getScanNum());
        if (scanType != null) {
            spec.setActivationMethod(scanType.getActivationMethod());
            spec.setIsHighPrecision(scanType.isHighPrecision());
            spec.setMsLevel(scanType.getMsLevel());
            spec.setRt(scanType.getScanStartTime());
            spec.setRtIsSeconds(false);
        }
        return spec;
    }
}
