package omics.msgf.params;

/**
 * Parameter
 */
public abstract class Parameter
{
    private String key;
    private String name;
    private String description;
    private boolean isOptional = false;
    private boolean isValueAssigned = false;
    private String additionalDescription = null;

    private boolean hidden = false;

    protected Parameter(String key, String name, String description)
    {
        this.key = key;
        this.name = name;
        this.description = description;
    }

    protected void setOptional()
    {
        this.isOptional = true;
    }

    public void setHidden()
    {
        this.hidden = true;
    }

    public String getKey()
    {
        return key;
    }

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }

    public String getAdditionalDescription()
    {
        return additionalDescription;
    }

    /**
     * set the detail description of this parameter
     *
     * @param additionalDescription description
     */
    public void setAdditionalDescription(String additionalDescription)
    {
        this.additionalDescription = additionalDescription;
    }

    public boolean isOptional()
    {
        return isOptional;
    }

    public boolean isHidden()
    {
        return hidden;
    }

    public String toString()
    {
        String usage = "-" + getKey() + " " + getName();
        if (isOptional())
            usage = "[" + usage + "]";
        usage = usage + " " + "(" + getDescription() + ")";
        return usage;
    }

    public void setValueAssigned()
    {
        this.isValueAssigned = true;
    }

    public boolean isValueAssigned()
    {
        return isValueAssigned;
    }

    public boolean isValid()
    {
        return !(!isOptional && !isValueAssigned());
    }

    public abstract String parse(String value);

    public abstract String getValueAsString();
}
