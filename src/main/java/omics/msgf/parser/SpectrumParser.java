package omics.msgf.parser;

import omics.msgf.msutil.Spectrum;
import omics.msgf.msutil.SpectrumMetaInfo;

import java.util.Map;

public interface SpectrumParser
{
    Spectrum readSpectrum(LineReader lineReader);

    Map<Integer, SpectrumMetaInfo> getSpecMetaInfoMap(
            BufferedRandomAccessLineReader lineReader);    // specIndex -> filePos
}
