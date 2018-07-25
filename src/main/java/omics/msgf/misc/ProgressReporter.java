package omics.msgf.misc;

/**
 * @author bryson
 */
public interface ProgressReporter
{
    /**
     * @return current progress
     */
    ProgressData getProgressData();

    void setProgressData(ProgressData data);
}
