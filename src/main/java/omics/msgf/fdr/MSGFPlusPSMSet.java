package omics.msgf.fdr;

import omics.msgf.msdbsearch.CompactSuffixArray;
import omics.msgf.msdbsearch.DatabaseMatch;
import omics.msgf.msdbsearch.MSGFPlusMatch;
import omics.msgf.ui.MSGFPlus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MSGFPlusPSMSet extends PSMSet
{
    private final List<MSGFPlusMatch> msgfPlusPSMList;
    private final boolean isDecoy;
    private final CompactSuffixArray sa;

    private boolean considerBestMatchOnly = false;

    /**
     * Construct with MSGFPlusMatch list.
     *
     * @param msgfPlusPSMList total PSMList, including target and decoy matches.
     * @param isDecoy         true if this PSMSet is decoy
     * @param sa              {@link CompactSuffixArray}
     */
    public MSGFPlusPSMSet(List<MSGFPlusMatch> msgfPlusPSMList, boolean isDecoy, CompactSuffixArray sa)
    {
        this.msgfPlusPSMList = msgfPlusPSMList;
        this.isDecoy = isDecoy;
        this.sa = sa;
    }

    public MSGFPlusPSMSet setConsiderBestMatchOnly(boolean considerBestMatchOnly)
    {
        this.considerBestMatchOnly = considerBestMatchOnly;
        return this;
    }

    @Override
    public boolean isGreaterBetter()
    {
        return false;
    }

    /**
     * Get corresponding PSM list from the total PSM list.
     */
    @Override
    public void read()
    {
        psmList = new ArrayList<>();
        peptideScoreTable = new HashMap<>();

        for (MSGFPlusMatch match : msgfPlusPSMList) {
            List<DatabaseMatch> dbMatchList;
            if (considerBestMatchOnly) {
                dbMatchList = new ArrayList<>();
                dbMatchList.add(match.getBestDBMatch());
            } else
                dbMatchList = match.getMatchList();

            for (DatabaseMatch m : dbMatchList) {
                String pepSeq = m.getPepSeq();

                boolean isDecoy = true;
                for (int index : m.getIndices()) {
                    String protAcc = sa.getSequence().getAnnotation(index);
                    if (!protAcc.startsWith(MSGFPlus.DECOY_PROTEIN_PREFIX)) {
                        isDecoy = false;
                        break;
                    }
                }

                if (this.isDecoy != isDecoy)
                    continue;

                float specEValue = (float) m.getSpecEValue();
                psmList.add(new ScoredString(pepSeq, specEValue));

                Float prevSpecEValue = peptideScoreTable.get(pepSeq);
                if (prevSpecEValue == null || specEValue < prevSpecEValue)
                    peptideScoreTable.put(pepSeq, specEValue);
            }
        }
    }

}
