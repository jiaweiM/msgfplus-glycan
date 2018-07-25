package omics.msgf.msutil;

import omics.msgf.mzid.Constants;
import omics.msgf.mzml.MzMLAdapter;
import omics.msgf.mzml.MzMLSpectraIterator;
import omics.msgf.mzml.MzMLSpectraMap;
import omics.msgf.parser.*;
import uk.ac.ebi.jmzidml.model.mzidml.CvParam;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;

public class SpectraAccessor
{
    private final File specFile;
    private final SpecFileFormat specFormat;
    SpectrumAccessorBySpecIndex specMap = null;
    Iterator<Spectrum> specItr = null;
    private MzMLAdapter mzmlAdapter = null;

    public SpectraAccessor(File specFile)
    {
        this(specFile, SpecFileFormat.getSpecFileFormat(specFile.getName()));
    }

    public SpectraAccessor(File specFile, SpecFileFormat specFormat)
    {
        this.specFile = specFile;
        this.specFormat = specFormat;
    }

    public SpectrumAccessorBySpecIndex getSpecMap()
    {
        if (specMap == null) {
            if (specFormat == SpecFileFormat.MZXML)
                specMap = new MzXMLSpectraMap(specFile.getPath());
            else if (specFormat == SpecFileFormat.MZML) {
                if (mzmlAdapter == null)
                    mzmlAdapter = new MzMLAdapter(specFile);
                specMap = new MzMLSpectraMap(mzmlAdapter);
            } else if (specFormat == SpecFileFormat.DTA_TXT)
                specMap = new PNNLSpectraMap(specFile.getPath());
            else {
                SpectrumParser parser = null;
                if (specFormat == SpecFileFormat.MGF)
                    parser = new MgfSpectrumParser();
                else if (specFormat == SpecFileFormat.MS2)
                    parser = new MS2SpectrumParser();
                else if (specFormat == SpecFileFormat.PKL)
                    parser = new PklSpectrumParser();
                else
                    return null;
                specMap = new SpectraMap(specFile.getPath(), parser);
            }
        }

        if (specMap == null) {
            System.out.println("File: " + specFile.getAbsolutePath());
            System.out.println("Format: " + specFormat.getPSIName());
        }
        return specMap;
    }

    public Iterator<Spectrum> getSpecItr()
    {
        if (specItr == null) {
            if (specFormat == SpecFileFormat.MZXML)
                specItr = new MzXMLSpectraIterator(specFile.getPath());
            else if (specFormat == SpecFileFormat.MZML) {
                if (mzmlAdapter == null)
                    mzmlAdapter = new MzMLAdapter(specFile);
                specItr = new MzMLSpectraIterator(mzmlAdapter);
            } else if (specFormat == SpecFileFormat.DTA_TXT)
                try {
                    specItr = new PNNLSpectraIterator(specFile.getPath());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            else {
                SpectrumParser parser = null;
                if (specFormat == SpecFileFormat.MGF)
                    parser = new MgfSpectrumParser();
                else if (specFormat == SpecFileFormat.MS2)
                    parser = new MS2SpectrumParser();
                else if (specFormat == SpecFileFormat.PKL)
                    parser = new PklSpectrumParser();
                else
                    return null;
                try {
                    specItr = new SpectraIterator(specFile.getPath(), parser);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

        return specItr;
    }

    public Spectrum getSpectrumBySpecIndex(int specIndex)
    {
        return getSpecMap().getSpectrumBySpecIndex(specIndex);
    }

    public Spectrum getSpectrumById(String specId)
    {
        return getSpecMap().getSpectrumById(specId);
    }

    public String getID(int specIndex)
    {
        return getSpecMap().getID(specIndex);
    }

    public float getPrecursorMz(int specIndex)
    {
        return getSpecMap().getPrecursorMz(specIndex);
    }

    public String getTitle(int specIndex)
    {
        return getSpecMap().getTitle(specIndex);
    }

    public CvParam getSpectrumIDFormatCvParam()
    {
        CvParam cvParam = null;
        if (specFormat == SpecFileFormat.DTA_TXT
                || specFormat == SpecFileFormat.MGF
                || specFormat == SpecFileFormat.PKL
                || specFormat == SpecFileFormat.MS2
                )
            cvParam = Constants.makeCvParam("MS:1000774", "multiple peak list nativeID format");
        else if (specFormat == SpecFileFormat.MZXML)
            cvParam = Constants.makeCvParam("MS:1000776", "scan number only nativeID format");
        else if (specFormat == SpecFileFormat.MZDATA)
            cvParam = Constants.makeCvParam("MS:1000777", "spectrum identifier nativeID format");
        else if (specFormat == SpecFileFormat.MZML) {
            if (mzmlAdapter == null)
                mzmlAdapter = new MzMLAdapter(specFile);
            cvParam = mzmlAdapter.getSpectrumIDFormatCvParam();
        }

        return cvParam;
    }

}
