package omics.msgf.msgf;

import omics.msgf.msutil.ActivationMethod;
import omics.msgf.msutil.Matter;
import omics.msgf.msutil.Peak;

public interface ScoredSpectrum<T extends Matter>
{
    int getNodeScore(T prm, T srm);

    float getNodeScore(T node, boolean isPrefix);

    int getEdgeScore(T curNode, T prevNode, float edgeMass);

    boolean getMainIonDirection();    // true: prefix, false: suffix

    Peak getPrecursorPeak();

    ActivationMethod[] getActivationMethodArr();

    int[] getScanNumArr();
}
