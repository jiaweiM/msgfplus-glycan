package omics.msgf.msutil;

import omics.msgf.params.ParamObject;
import omics.msgf.params.UserParam;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * This class represents an enzyme.
 *
 * @author sangtaekim
 */
public class Enzyme implements ParamObject
{

    /**
     * Unspecific cleavage enzyme (can cleavage after any residue)
     */
    public static final Enzyme UnspecificCleavage;
    /**
     * TRYPSIN enzyme (cleave after K or R)
     */
    public static final Enzyme TRYPSIN;
    /**
     * CHYMOTRYPSIN enzyme (cleave after FYWL)
     */
    public static final Enzyme CHYMOTRYPSIN;
    /**
     * LysC enzyme (cleave after K)
     */
    public static final Enzyme LysC;
    /**
     * LysN enzyme (cleave before K)
     */
    public static final Enzyme LysN;
    /**
     * GluC enzyme (cleave after E)
     */
    public static final Enzyme GluC;
    /**
     * ArgC enzyme (cleave after R)
     */
    public static final Enzyme ArgC;
    /**
     * AspN enzyme (cleave before D)
     */
    public static final Enzyme AspN;
    /**
     * ALP enzyme
     */
    public static final Enzyme ALP;
    /**
     * Endogenous peptides (do not cleave after any residue, i.e. no internal cleavage)
     */
    public static final Enzyme NoCleavage;
    private static HashMap<String, Enzyme> enzymeTable;
    private static ArrayList<Enzyme> registeredEnzymeList;

    static {
        UnspecificCleavage = new Enzyme("UnspecificCleavage", null, false, "unspecific cleavage", "MS:1001956");
        TRYPSIN = new Enzyme("Tryp", "KR", false, "Trypsin", "MS:1001251");
//		TRYPSIN.setNeighboringAAEfficiency(0.9148273f);
//		TRYPSIN.setPeptideCleavageEffiency(0.98173124f);

//		TRYPSIN.setNeighboringAAEfficiency(0.9523f);
//		TRYPSIN.setPeptideCleavageEffiency(0.9742f);

        // Modified by Sangtae to boost the performance
        TRYPSIN.setNeighboringAAEfficiency(0.99999f);
        TRYPSIN.setPeptideCleavageEffiency(0.99999f);

        CHYMOTRYPSIN = new Enzyme("Chymotrypsin", "FYWL", false, "Chymotrypsin", "MS:1001306");

        LysC = new Enzyme("LysC", "K", false, "Lys-C", "MS:1001309");
//		LysC.setNeighboringAAEfficiency(0.79f);
//		LysC.setPeptideCleavageEffiency(0.89f);
        LysC.setNeighboringAAEfficiency(0.999f);
        LysC.setPeptideCleavageEffiency(0.999f);

        LysN = new Enzyme("LysN", "K", true, "Lys-N", null);
        LysN.setNeighboringAAEfficiency(0.79f);
        LysN.setPeptideCleavageEffiency(0.89f);

        GluC = new Enzyme("GluC", "E", false, "glutamyl endopeptidase", "MS:1001917");
        ArgC = new Enzyme("ArgC", "R", false, "Arg-C", "MS:1001303");
        AspN = new Enzyme("AspN", "D", true, "Asp-N", "MS:1001304");

        ALP = new Enzyme("aLP", null, false, "alphaLP", null);

        // NoCleavage aka no internal cleavage
        // Do not allow cleavage after any residue
        NoCleavage = new Enzyme("NoCleavage", null, false, "no cleavage", "MS:1001955");

        enzymeTable = new HashMap<String, Enzyme>();
        registeredEnzymeList = new ArrayList<Enzyme>();

        // Do not include "UnspecificCleavage" in HashMap enzymeTable
        registeredEnzymeList.add(UnspecificCleavage); // 0

//		register(UnspecificCleavage.name, UnspecificCleavage);
        register(TRYPSIN.name, TRYPSIN);              // 1
        register(CHYMOTRYPSIN.name, CHYMOTRYPSIN);    // 2
        register(LysC.name, LysC);                    // 3
        register(LysN.name, LysN);                    // 4
        register(GluC.name, GluC);                    // 5
        register(ArgC.name, ArgC);                    // 6
        register(AspN.name, AspN);                    // 7
        register(ALP.name, ALP);                      // 8
        register(NoCleavage.name, NoCleavage);        // 9

        // Add user-defined enzymes
        File enzymeFile = new File("params/enzymes.txt");
        if (enzymeFile.exists()) {
//			System.out.println("Loading " + enzymeFile.getAbsolutePath());
            ArrayList<String> paramStrs = UserParam.parseFromFile(enzymeFile.getPath(), 4);
            for (String paramStr : paramStrs) {
                String[] token = paramStr.split(",");
                String shortName = token[0];
                String cleaveAt = token[1];
                if (cleaveAt.equalsIgnoreCase("null"))
                    cleaveAt = null;
                else {
                    for (int i = 0; i < cleaveAt.length(); i++) {
                        if (!AminoAcid.isStdAminoAcid(cleaveAt.charAt(i))) {
                            System.err.println("Invalid user-defined enzyme at " + enzymeFile.getAbsolutePath() + ": " + paramStr);
                            System.err.println("Unrecognizable aa residue: " + cleaveAt.charAt(i));
                            System.exit(-1);
                        }
                    }
                }
                boolean isNTerm = false;    // C-Term: false, N-term: true
                if (token[2].equals("C"))
                    isNTerm = false;
                else if (token[2].equals("N"))
                    isNTerm = true;
                else {
                    System.err.println("Invalid user-defined enzyme at " + enzymeFile.getAbsolutePath() + ": " + paramStr);
                    System.err.println(token[2] + " must be 'C' or 'N'.");
                    System.exit(-1);
                }
                String description = token[3];

                Enzyme userEnzyme = new Enzyme(shortName, cleaveAt, isNTerm, description, null);
                register(shortName, userEnzyme);
            }
        }
    }

    /**
     * True if the enzyme cleaves n-terminus, false otherwise.
     */
    private boolean isNTerm;
    /**
     * Name of the enzyme.
     */
    private String name;
    /**
     * Description
     */
    private String description;
    /**
     * Amino acid residues cleaved by the enzyme.
     */
    private char[] residues;
    /**
     * Tracks whether a residue is cleavable Residue symbols as chars are converted to their ASCII value when updating
     * this array For example, the cleavability of residue K is tracked at isResidueCleavable[82]
     */
    private boolean[] isResidueCleavable;
    // the probability that a peptide generated by this enzyme follows the cleavage rule
    // E.g. for trypsin, probability that a peptide ends with K or R
    private float peptideCleavageEfficiency = 0;
    // the probability that a neighboring amino acid follows the enzyme rule
    // E.g. for trypsin, probability that the preceding amino acid is K or R
    private float neighboringAACleavageEfficiency = 0;
    private String psiCvAccession;

    /**
     * Instantiates a new enzyme.
     *
     * @param name     the name
     * @param residues the residues cleaved by the enzyme (String)
     * @param isNTerm  N term or C term (true if it cleaves N-term)
     */
    private Enzyme(String name, String residues, boolean isNTerm, String description, String psiCvAccession)
    {
        this.name = name;
        this.description = description;

        /*
         * null is passed as the residue string for both non-specific and
         * "no cleavage", so in order to distinguish the desired behavior we
         * inspect the controlled vocabulary name of the enzyme to determine
         * if it is "no cleavage"
         *
         */
        if (psiCvAccession != null && psiCvAccession.equals("MS:1001955")) {
            // NoCleavage aka no internal cleavage
            this.residues = new char[0];
            this.isResidueCleavable = new boolean[128];
        } else if (residues != null) {
            this.residues = new char[residues.length()];
            this.isResidueCleavable = new boolean[128];
            for (int i = 0; i < residues.length(); i++) {
                char residue = residues.charAt(i);
                if (!Character.isUpperCase(residue)) {
                    System.err.println("Enzyme residues must be upper cases: " + residue);
                    System.exit(-1);
                }
                this.residues[i] = residue;
                isResidueCleavable[residue] = true;
            }
        }
        this.isNTerm = isNTerm;
        this.psiCvAccession = psiCvAccession;
    }

    /**
     * Get an Enzyme by enzyme name
     */
    public static Enzyme getEnzymeByName(String name)
    {
        return enzymeTable.get(name);
    }

    /**
     * Get all registered enzymes
     */
    public static Enzyme[] getAllRegisteredEnzymes()
    {
        return registeredEnzymeList.toArray(new Enzyme[0]);
    }

    /**
     * Obsolete method; does nothing
     */
    public static Enzyme register(String name, String residues, boolean isNTerm, String description)
    {
        return null;
    }

    private static void register(String name, Enzyme enzyme)
    {
        if (enzymeTable.put(name, enzyme) == null)
            registeredEnzymeList.add(enzyme);
    }

    /**
     * Sets the neighboring amino acid efficiency as the probability that a neighboring amino acid follows the enzyme
     * rule
     *
     * @param neighboringAACleavageEfficiency neighboring amino acid effieicncy
     * @return this object
     */
    private void setNeighboringAAEfficiency(float neighboringAACleavageEfficiency)
    {
        this.neighboringAACleavageEfficiency = neighboringAACleavageEfficiency;
    }

    /**
     * Gets the neighboring amino acid efficiency
     *
     * @return neighboring amino acid efficiency
     */
    public float getNeighboringAACleavageEffiency()
    {
        return neighboringAACleavageEfficiency;
    }

    /**
     * Sets the peptide cleavage efficiency as the probability that a peptide generated by this enzyme follows the
     * cleavage rule
     *
     * @param peptideCleavageEfficiency peptide cleagave efficiency
     * @return this object
     */
    private void setPeptideCleavageEffiency(float peptideCleavageEfficiency)
    {
        this.peptideCleavageEfficiency = peptideCleavageEfficiency;
    }

    /**
     * Gets the peptide efficiency.
     *
     * @return peptide efficiency
     */
    public float getPeptideCleavageEfficiency()
    {
        return peptideCleavageEfficiency;
    }

    /**
     * Returns the name of the enzyme.
     *
     * @return the name of the enzyme.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Returns the description of the enzyme.
     *
     * @return the description of the enzyme.
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * Returns the description of the enzyme when it is showed in the usage info.
     *
     * @return the description of the enzyme when it is showed in the usage info.
     */
    public String getParamDescription()
    {
        return description;
    }

    /**
     * Checks if this enzyme cleaves N term.
     *
     * @return true, if it cleaves N term.
     */
    public boolean isNTerm()
    {
        return isNTerm;
    }

    /**
     * Checks if this enzyme cleaves C term.
     *
     * @return true, if it cleaves C term.
     */
    public boolean isCTerm()
    {
        return !isNTerm;
    }

    /**
     * Checks if the amino acid is cleavable.
     *
     * @param aa the amino acid
     * @return true, if aa is cleavable
     */
    public boolean isCleavable(AminoAcid aa)
    {
        if (this.residues == null)
            return true;
        for (char r : this.residues)
            if (r == aa.getUnmodResidue())
                return true;
        return false;
    }

    /**
     * Checks if the amino acid is cleavable.
     *
     * @param residue amino acid residue
     * @return true, if residue is cleavable
     */
    public boolean isCleavable(char residue)
    {
        if (isResidueCleavable == null)
            return true;
        return isResidueCleavable[residue];
    }

    /**
     * Checks if the peptide is cleaved by the enzyme. Does not check for exception residues (meaning K.P or K.P is
     * considered cleavable for trypsin)
     *
     * @param p peptide
     * @return true if p is cleaved, false otherwise.
     */
    public boolean isCleaved(Peptide p)
    {
        AminoAcid aa;
        if (isNTerm)
            aa = p.get(0);
        else
            aa = p.get(p.size() - 1);
        return isCleavable(aa.getResidue());
    }

    /**
     * Returns PSI CV accession.
     *
     * @return HUPO PSI CV accession of this enzyme. null if unknown.
     */
    public String getPSICvAccession()
    {
        return this.psiCvAccession;
    }

    /**
     * Returns the number of cleavaged terminii
     *
     * @param annotation annotation (e.g. K.DLFGEK.I)
     * @return the number of cleavaged terminii
     */
    public int getNumCleavedTermini(String annotation, AminoAcidSet aaSet)
    {
        int nCT = 0;
        String pepStr = annotation.substring(annotation.indexOf('.') + 1, annotation.lastIndexOf('.'));
        Peptide peptide = aaSet.getPeptide(pepStr);

        // Check whether the C-terminus of the peptide is a cleavage point
        if (this.isCleaved(peptide))
            nCT++;

        if (this.isNTerm) {
            // N-terminal cleavage, including AspN
            AminoAcid nextAA = aaSet.getAminoAcid(annotation.charAt(annotation.length() - 1));
            if (nextAA == null || this.isCleavable(nextAA))
                nCT++;
        } else {
            // C-terminal cleavage, including trypsin
            AminoAcid precedingAA = aaSet.getAminoAcid(annotation.charAt(0));
            if (precedingAA == null || this.isCleavable(precedingAA))
                nCT++;
        }

        return nCT;
    }

    @Override
    public int hashCode()
    {
        return name.hashCode();
    }

    /**
     * Gets the residues.
     *
     * @return the residues
     */
    public char[] getResidues()
    {
        return residues;
    }
}