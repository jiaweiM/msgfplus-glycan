package omics.msgf.msutil;

import java.util.HashMap;

public class VolatileAminoAcid extends AminoAcid
{

    private static HashMap<Float, AminoAcid> table = new HashMap<Float, AminoAcid>();

    private VolatileAminoAcid(float mass)
    {
        super('*', String.format("(%.3f)", mass), mass);
    }

    public static AminoAcid getVolatileAminoAcid(float mass)
    {
        AminoAcid aa = table.get(mass);
        if (aa == null) {
//			System.out.println("Register " + mass);
            aa = new VolatileAminoAcid(mass);
            table.put(mass, aa);
        }
        return aa;
    }

    @Override
    public String getResidueStr()
    {
        return super.getName();
    }

    @Override
    public boolean isModified()
    {
        return true;
    }
}
