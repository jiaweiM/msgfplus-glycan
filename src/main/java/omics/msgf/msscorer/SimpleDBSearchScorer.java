package omics.msgf.msscorer;

import omics.msgf.msgf.ScoredSpectrum;
import omics.msgf.msutil.Matter;

public interface SimpleDBSearchScorer<T extends Matter> extends ScoredSpectrum<T>
{
    // fromIndex: inclusive, toIndex: exclusive
    int getScore(double[] prefixMassArr, int[] intPrefixMassArr, int fromIndex, int toIndex, int numMods);
}
