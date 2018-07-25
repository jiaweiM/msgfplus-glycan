package omics.msgf.msutil;

import omics.msgf.params.ParamObject;
import omics.msgf.params.UserParam;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;


public class Protocol implements ParamObject
{
    public static final Protocol AUTOMATIC;
    public static final Protocol PHOSPHORYLATION;
    public static final Protocol ITRAQ;
    public static final Protocol ITRAQPHOSPHO;
    public static final Protocol TMT;
    public static final Protocol STANDARD;
    private static HashMap<String, Protocol> table;
    private static ArrayList<Protocol> protocolList;

    static {
        AUTOMATIC = new Protocol("Automatic", "Automatic");
        PHOSPHORYLATION = new Protocol("Phosphorylation", "Phospho-enriched");
        ITRAQ = new Protocol("iTRAQ", "iTRAQ");
        ITRAQPHOSPHO = new Protocol("iTRAQPhospho", "iTRAQPhospho");
        TMT = new Protocol("TMT", "TMT");
        STANDARD = new Protocol("Standard", "Standard");

        table = new HashMap<>();
        protocolList = new ArrayList<>();

        protocolList.add(AUTOMATIC);
        add(PHOSPHORYLATION);
        add(ITRAQ);
        add(ITRAQPHOSPHO);
        add(TMT);
        add(STANDARD);

        // Parse activation methods defined by a user
        File protocolFile = new File("params/protocols.txt");
        if (protocolFile.exists()) {
            ArrayList<String> paramStrs = UserParam.parseFromFile(protocolFile.getPath(), 2);
            for (String paramStr : paramStrs) {
                String[] token = paramStr.split(",");
                String shortName = token[0];
                String description = token[1];
                Protocol newProt = new Protocol(shortName, description);
                add(newProt);
            }
        }
    }

    private String name;
    private String description;

    private Protocol(String name, String description)
    {
        this.name = name;
        this.description = description;
    }

    // static members
    public static Protocol get(String name)
    {
        return table.get(name);
    }

    public static Protocol[] getAllRegisteredProtocols()
    {
        return protocolList.toArray(new Protocol[0]);
    }

    private static void add(Protocol prot)
    {
        if (table.put(prot.name, prot) == null)
            protocolList.add(prot);
    }

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }

    public String getParamDescription()
    {
        return name;
    }

}
