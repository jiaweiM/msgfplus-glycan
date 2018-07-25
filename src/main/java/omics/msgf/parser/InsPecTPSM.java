package omics.msgf.parser;

import omics.msgf.msutil.AminoAcid;
import omics.msgf.msutil.AminoAcidSet;
import omics.msgf.msutil.Annotation;
import omics.msgf.msutil.Peptide;

import java.util.ArrayList;

public class InsPecTPSM extends PSM
{
    private String insPecTString;
    private long specFilePos;
    private AminoAcid precedingAA;
    private AminoAcid succeedingAA;
    private ArrayList<Integer> scanNumList;

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

    public Annotation getAnnotation()
    {
        return new Annotation(precedingAA, super.getPeptide(), succeedingAA);
    }

    public String getInsPecTString()
    {
        return insPecTString;
    }

    public void setInsPecTString(String insPecTString)
    {
        this.insPecTString = insPecTString;
    }

    public long getSpecFilePos()
    {
        return specFilePos;
    }

    public void setSpecFilePos(long specFilePos)
    {
        this.specFilePos = specFilePos;
    }

    public ArrayList<Integer> getScanNumList()
    {
        return scanNumList;
    }

    public void setScanNumList(ArrayList<Integer> scanNumList)
    {
        this.scanNumList = scanNumList;
    }

    public AminoAcidSet getAASet(AminoAcidSet baseAASet)
    {
        Peptide peptide = getPeptide();
        if (peptide != null && peptide.isModified()) {
            ArrayList<AminoAcid> modAAList = new ArrayList<AminoAcid>();
            for (AminoAcid aa : peptide) {
                if (aa.isModified())    // modified residue
                {
                    modAAList.add(aa);
                }
            }
            return AminoAcidSet.getAminoAcidSetFromModAAList(baseAASet, modAAList);
        } else
            return baseAASet;
    }

}
