package omics.msgf.msdbsearch;

import omics.msgf.msutil.ActivationMethod;

import java.util.SortedSet;
import java.util.TreeSet;

/**
 * a single PSM Match
 */
public class DatabaseMatch extends Match
{
    private int index;
    private byte length;

    // optional
    private boolean isProteinNTerm;
    private boolean isProteinCTerm;
    private boolean isNTermMetCleaved = false;

    private Float psmQValue = null;
    private Float pepQValue = null;

    // for degenerate peptides
    private SortedSet<Integer> indices;

    public DatabaseMatch(
            int index,
            byte length,
            int score,
            float peptideMass,
            int nominalPeptideMass,
            int charge,
            String pepSeq,
            ActivationMethod[] actMethodArr
    )
    {
        super(score, peptideMass, nominalPeptideMass, charge, pepSeq, actMethodArr);
        this.index = index;
        this.length = length;
        isProteinNTerm = false;
        isProteinCTerm = false;
    }

    public boolean isNTermMetCleaved()
    {
        return this.isNTermMetCleaved;
    }

    public DatabaseMatch setNTermMetCleaved(boolean isNTermMetCleaved)
    {
        this.isNTermMetCleaved = isNTermMetCleaved;
        return this;
    }

    public Float getPSMQValue()
    {
        return this.psmQValue;
    }

    public void setPSMQValue(float psmQValue)
    {
        this.psmQValue = psmQValue;
    }

    public Float getPepQValue()
    {
        return this.pepQValue;
    }

    public void setPepQValue(Float pepQValue)
    {
        this.pepQValue = pepQValue;
    }

    public void addIndex(int index)
    {
        if (indices == null) {
            indices = new TreeSet<>();
            indices.add(this.index);
        }
        indices.add(index);
    }

    public SortedSet<Integer> getIndices()
    {
        if (indices == null) {
            SortedSet<Integer> temp = new TreeSet<>();
            temp.add(index);
            return temp;
        }
        return indices;
    }

    public int getIndex()
    {
        return index;
    }

    public int getLength()
    {
        return length;
    }

    public boolean isProteinNTerm()
    {
        return isProteinNTerm;
    }

    public DatabaseMatch setProteinNTerm(boolean isProteinNTerm)
    {
        this.isProteinNTerm = isProteinNTerm;
        return this;
    }

    public boolean isProteinCTerm()
    {
        return isProteinCTerm;
    }

    public DatabaseMatch setProteinCTerm(boolean isProteinCTerm)
    {
        this.isProteinCTerm = isProteinCTerm;
        return this;
    }

    public int hashCode()
    {
        return index * length;
    }

    public boolean equals(Object obj)
    {
        if (obj instanceof DatabaseMatch) {
            DatabaseMatch other = (DatabaseMatch) obj;
            if (index == other.index && length == other.length)
                return true;
        }
        return false;
    }
}
