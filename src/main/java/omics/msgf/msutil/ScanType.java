package omics.msgf.msutil;

public class ScanType
{
    private ActivationMethod activationMethod;
    private int msLevel;
    private boolean isHighPrecision;
    private float scanStartTime;

    public ScanType(ActivationMethod activationMethod, boolean isHighPrecision, int msLevel)
    {
        this.activationMethod = activationMethod;
        this.msLevel = msLevel;
        this.isHighPrecision = isHighPrecision;
    }

    public ScanType(ActivationMethod activationMethod, boolean isHighPrecision, int msLevel, float scanStartTime)
    {
        this.activationMethod = activationMethod;
        this.msLevel = msLevel;
        this.isHighPrecision = isHighPrecision;
        this.scanStartTime = scanStartTime;
    }

    public ActivationMethod getActivationMethod()
    {
        return activationMethod;
    }

    public int getMsLevel()
    {
        return msLevel;
    }

    public boolean isHighPrecision()
    {
        return isHighPrecision;
    }

    public float getScanStartTime()
    {
        return scanStartTime;
    }
}

