package omics.msgf.msutil;

import omics.msgf.mzid.Constants;
import omics.msgf.params.ParamObject;
import omics.msgf.params.UserParam;
import uk.ac.ebi.jmzidml.model.mzidml.CvParam;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;


public class ActivationMethod implements ParamObject
{
    public static final ActivationMethod ASWRITTEN;
    public static final ActivationMethod CID;
    public static final ActivationMethod ETD;
    public static final ActivationMethod HCD;
    public static final ActivationMethod PQD;
    public static final ActivationMethod FUSION;
    public static final ActivationMethod UVPD;
    private static HashMap<String, ActivationMethod> table;
    private static HashMap<String, ActivationMethod> cvTable;
    private static ArrayList<ActivationMethod> registeredActMethods;

    static {
        ASWRITTEN = new ActivationMethod("As written in the spectrum or CID if no info", "as written in the spectrum or CID if no info");
        CID = new ActivationMethod("CID", "collision-induced dissociation", "MS:1000133");
        ETD = new ActivationMethod("ETD", "electron transfer dissociation", "MS:1000598").electronBased();
        HCD = new ActivationMethod("HCD", "high-energy collision-induced dissociation", "MS:1000422");
        FUSION = new ActivationMethod("Merge spectra from the same precursor", "Merge spectra from the same precursor");
        PQD = new ActivationMethod("PQD", "pulsed q dissociation", "MS:1000599");
        UVPD = new ActivationMethod("UVPD", "Ultraviolet photo dissociation.", "MS:1000435");    // Photodissociation ontology term for now

        table = new HashMap<String, ActivationMethod>();

        registeredActMethods = new ArrayList<ActivationMethod>();

        // Fragmentation Method
        addToList(ASWRITTEN);    // -m 0
        add(CID);                // -m 1
        add(ETD);                // -m 2
        add(HCD);                // -m 3
        addToList(FUSION);        // -m 4
        addAlias("ETD+SA", ETD);
        add(UVPD);                // -m 5

        // Parse activation methods defined by a user
        File actMethodFile = new File("params/activationMethods.txt");
        if (actMethodFile.exists()) {
//			System.out.println("Loading " + actMethodFile.getAbsolutePath());
            ArrayList<String> paramStrs = UserParam.parseFromFile(actMethodFile.getPath(), 2);
            for (String paramStr : paramStrs) {
                String[] token = paramStr.split(",");
                String shortName = token[0];
                String fullName = token[1];
                ActivationMethod newMethod = new ActivationMethod(shortName, fullName);
                add(newMethod);
            }
        }

        cvTable = new HashMap<String, ActivationMethod>();
        cvTable.put("MS:1000133", CID);
        cvTable.put("MS:1000598", ETD);
        cvTable.put("MS:1000422", HCD);
        cvTable.put("MS:1000599", PQD);
    }

    private final String name;
    private String fullName;
    private boolean electronBased = false;
    private String accession;
    private CvParam cvParam;
    private ActivationMethod(String name, String fullName)
    {
        this(name, fullName, null);
    }
    private ActivationMethod(String name, String fullName, String accession)
    {
        this.name = name;
        this.fullName = fullName;
        this.accession = accession;
        if (accession != null)
            this.cvParam = Constants.makeCvParam(accession, fullName);
    }

    public static ActivationMethod get(String name)
    {
        return table.get(name);
    }

    public static ActivationMethod getByCV(String cvAccession)
    {
        return cvTable.get(cvAccession);
    }

    public static ActivationMethod register(String name, String fullName)
    {
        ActivationMethod m = table.get(name);
        if (m != null)
            return m;    // registration was not successful
        else {
            ActivationMethod newMethod = new ActivationMethod(name, fullName);
            table.put(name, newMethod);
            return newMethod;
        }
    }

    //// static /////////////
    public static ActivationMethod[] getAllRegisteredActivationMethods()
    {
        return registeredActMethods.toArray(new ActivationMethod[0]);
    }

    private static void add(ActivationMethod actMethod)
    {
        if (table.put(actMethod.name, actMethod) == null)
            registeredActMethods.add(actMethod);
    }

    // add to the HashMap only
    private static void addAlias(String name, ActivationMethod actMethod)
    {
        table.put(name, actMethod);
    }

    // add to the list only
    private static void addToList(ActivationMethod actMethod)
    {
        registeredActMethods.add(actMethod);
    }

    private ActivationMethod electronBased()
    {
        this.electronBased = true;
        return this;
    }

    public String getName()
    {
        return name;
    }

    public String getFullName()
    {
        return fullName;
    }

    public String getParamDescription()
    {
        return name;
    }

    public String getPSICVAccession()
    {
        return accession;
    }

    public boolean isElectronBased()
    {
        return electronBased;
    }

    public CvParam getCvParam()
    {
        return cvParam;
    }

    @Override
    public String toString()
    {
        return name;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof ActivationMethod)
            return this.name.equalsIgnoreCase(((ActivationMethod) obj).name);
        return false;
    }

    @Override
    public int hashCode()
    {
        return this.name.hashCode();
    }
}
