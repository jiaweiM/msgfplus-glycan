package omics.msgf.msgf;

import omics.msgf.msutil.Matter;

import java.util.List;

public class ScoredSpectrumSumHP<T extends Matter> extends ScoredSpectrumSum<T>
{
    public ScoredSpectrumSumHP(List<ScoredSpectrum<T>> scoredSpecList)
    {
        super(scoredSpecList);
    }


}
