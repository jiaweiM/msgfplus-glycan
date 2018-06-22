package omics.msgf.mzid;

import omics.msgf.msgf.MSGFDBResultGenerator;
import omics.msgf.params.ParamManager;
import uk.ac.ebi.jmzidml.model.mzidml.*;

import java.util.List;
import java.util.Map;

public class MzIdAdapter
{
    private SpectrumIdentificationProtocol searchParams;

    private List<DBSequence> dbSeqList;
    private List<Peptide> pepList;
    private List<PeptideEvidence> pepEviList;
    private SpectrumIdentificationList idList;
    private Map<String, Peptide> pepIDToPeptide;    // peptide id to peptide object
    private Map<String, DBSequence> protIDToProtein;    // db sequence id to db sequence object

    public void addSearchParams(ParamManager params)
    {

    }

    // set-up
    public void addPeptideMatch(MSGFDBResultGenerator.DBMatch dbMatch)
    {

    }


}
