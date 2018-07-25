package omics.msgf.parser;

import omics.msgf.msutil.AminoAcid;
import omics.msgf.msutil.AminoAcidSet;
import omics.msgf.msutil.Peptide;

import java.util.ArrayList;


public class MSGappedDictionaryPSM extends PSM
{
    private AminoAcid precedingAA;
    private AminoAcid succeedingAA;
    private AminoAcidSet aaSet;
    private float parentMassError;
    //private boolean isPeptideModified = false; // remove next. PSM will take care of this

    public MSGappedDictionaryPSM aaSet(AminoAcidSet aaSet)
    {
        this.aaSet = aaSet;
        return this;
    }

    public MSGappedDictionaryPSM peptide(String peptideStr)
    {
        ArrayList<AminoAcid> aaList = new ArrayList<AminoAcid>();
        for (int i = 0; i < peptideStr.length(); i++)
            aaList.add(aaSet.getAminoAcid(peptideStr.charAt(i)));
        this.peptide(new Peptide(aaList));
        return this;
    }

    public boolean isPeptideModified()
    {
        for (int i = 0; i < this.getPeptideStr().length(); i++)
            if (Character.isLowerCase(this.getPeptideStr().charAt(i))) return true;
        return false;
    }

    public AminoAcid getPrecedingAA()
    {
        return precedingAA;
    }

    public void setPrecedingAA(AminoAcid precedingAA)
    {
        this.precedingAA = precedingAA;
    }

    public AminoAcid getSucceedingAA()
    {
        return succeedingAA;
    }

    public void setSucceedingAA(AminoAcid succeedingAA)
    {
        this.succeedingAA = succeedingAA;
    }

    public AminoAcidSet getAASet()
    {
        return aaSet;
    }

    public float getParentMassError()
    {
        return parentMassError;
    }

    public void setParentMassError(float error)
    {
        this.parentMassError = error;
    }
}
