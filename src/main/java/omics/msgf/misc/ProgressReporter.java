package omics.msgf.misc;

/**
 * @author bryson
 */
public interface ProgressReporter
{
    ProgressData getProgressData();

    void setProgressData(ProgressData data);
}
