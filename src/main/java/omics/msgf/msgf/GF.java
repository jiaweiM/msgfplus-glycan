package omics.msgf.msgf;

import omics.msgf.msutil.Annotation;
import omics.msgf.msutil.Matter;

public interface GF<T extends Matter>
{
    boolean computeGeneratingFunction();

    int getScore(Annotation annotation);

    double getSpectralProbability(int score);

    int getMaxScore();

    ScoreDist getScoreDist();
}
