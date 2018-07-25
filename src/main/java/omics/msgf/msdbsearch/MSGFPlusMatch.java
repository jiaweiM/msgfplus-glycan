package omics.msgf.msdbsearch;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Match of a spectrum
 */
public class MSGFPlusMatch implements Comparable<MSGFPlusMatch>
{
    private final int specIndex;
    private final List<DatabaseMatch> matchList;
    private final double specEValue;

    public MSGFPlusMatch(int specIndex, PriorityQueue<DatabaseMatch> matchQueue)
    {
        this.specIndex = specIndex;
        this.matchList = new ArrayList<>(matchQueue);
        matchList.sort(new Match.SpecProbComparator());
        specEValue = getBestDBMatch().getSpecEValue();
    }

    public DatabaseMatch getBestDBMatch()
    {
        return matchList.get(matchList.size() - 1);
    }

    public int getSpecIndex()
    {
        return specIndex;
    }

    public List<DatabaseMatch> getMatchList()
    {
        return matchList;
    }

    public double getSpecEValue()
    {
        return specEValue;
    }

    @Override
    public int compareTo(MSGFPlusMatch o)
    {
        if (specEValue < o.specEValue)
            return -1;
        else if (specEValue == o.specEValue)
            return 0;
        else
            return 1;
    }

}
