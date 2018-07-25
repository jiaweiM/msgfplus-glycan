package omics.msgf.msutil;

public class DBFileFormat extends FileFormat
{
    public static final DBFileFormat FASTA = new DBFileFormat(new String[]{".fa", ".fasta"});

    private DBFileFormat(String[] suffixes)
    {
        super(suffixes);
    }

    private DBFileFormat(String suffix)
    {
        super(suffix);
    }
}
