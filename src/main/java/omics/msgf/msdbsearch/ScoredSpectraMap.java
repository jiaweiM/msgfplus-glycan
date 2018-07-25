package omics.msgf.msdbsearch;

import omics.msgf.misc.ProgressData;
import omics.msgf.msgf.NominalMass;
import omics.msgf.msgf.ScoredSpectrum;
import omics.msgf.msgf.ScoredSpectrumSum;
import omics.msgf.msgf.Tolerance;
import omics.msgf.msscorer.*;
import omics.msgf.msscorer.NewScorerFactory.SpecDataType;
import omics.msgf.msutil.*;

import java.util.*;

public class ScoredSpectraMap
{
    private final SpectraAccessor specAcc;
    private final List<SpecKey> specKeyList;
    private final Tolerance leftParentMassTolerance;
    private final Tolerance rightParentMassTolerance;
    private final int minIsotopeError;
    private final int maxIsotopeError;
    private final SpecDataType specDataType;

    private SortedMap<Double, SpecKey> pepMassSpecKeyMap;
    private Map<SpecKey, SimpleDBSearchScorer<NominalMass>> specKeyScorerMap;
    private Map<Pair<Integer, Integer>, SpecKey> specIndexChargeToSpecKeyMap;

    private Map<SpecKey, NewRankScorer> specKeyRankScorerMap;

//	private Map<SpecKey,Tolerance> specKeyToleranceMap;

    private boolean turnOffEdgeScoring = false;

    private ProgressData progress;

    public ScoredSpectraMap(
            SpectraAccessor specAcc,
            List<SpecKey> specKeyList,
            Tolerance leftParentMassTolerance,
            Tolerance rightParentMassTolerance,
            int minIsotopeError,
            int maxIsotopeError,
            SpecDataType specDataType,
            boolean storeRankScorer,
            boolean supportSpectrumSpecificErrorTolerance
    )
    {
        this.specAcc = specAcc;
        this.specKeyList = specKeyList;
        this.leftParentMassTolerance = leftParentMassTolerance;
        this.rightParentMassTolerance = rightParentMassTolerance;
        this.minIsotopeError = minIsotopeError;
        this.maxIsotopeError = maxIsotopeError;
        this.specDataType = specDataType;

        pepMassSpecKeyMap = Collections.synchronizedSortedMap((new TreeMap<Double, SpecKey>()));
        specKeyScorerMap = Collections.synchronizedMap(new HashMap<SpecKey, SimpleDBSearchScorer<NominalMass>>());
        specIndexChargeToSpecKeyMap = Collections.synchronizedMap(new HashMap<Pair<Integer, Integer>, SpecKey>());

//		// To support spectrum-specific tolerance
//		if(supportSpectrumSpecificErrorTolerance)
//			specKeyToleranceMap = Collections.synchronizedMap(new HashMap<SpecKey,Tolerance>());

        if (storeRankScorer)
            specKeyRankScorerMap = Collections.synchronizedMap(new HashMap<SpecKey, NewRankScorer>());
        progress = null;
    }

    public ScoredSpectraMap(
            SpectraAccessor specAcc,
            List<SpecKey> specKeyList,
            Tolerance leftParentMassTolerance,
            Tolerance rightParentMassTolerance,
            int maxNum13C,
            SpecDataType specDataType,
            boolean storeRankScorer,
            boolean supportSpectrumSpecificErrorTolerance
    )
    {
        this(specAcc, specKeyList, leftParentMassTolerance, rightParentMassTolerance, 0, maxNum13C, specDataType, storeRankScorer, supportSpectrumSpecificErrorTolerance);
    }

    public ScoredSpectraMap(
            SpectraAccessor specAcc,
            List<SpecKey> specKeyList,
            Tolerance leftParentMassTolerance,
            Tolerance rightParentMassTolerance,
            int maxNum13C,
            SpecDataType specDataType,
            boolean storeRankScorer
    )
    {
        this(specAcc, specKeyList, leftParentMassTolerance, rightParentMassTolerance, 0, maxNum13C, specDataType, storeRankScorer, false);
    }

    public ScoredSpectraMap turnOffEdgeScoring()
    {
        this.turnOffEdgeScoring = true;
        return this;
    }

    public SortedMap<Double, SpecKey> getPepMassSpecKeyMap()
    {
        return pepMassSpecKeyMap;
    }

    public Map<SpecKey, SimpleDBSearchScorer<NominalMass>> getSpecKeyScorerMap()
    {
        return specKeyScorerMap;
    }

    public SpectraAccessor getSpectraAccessor()
    {
        return specAcc;
    }

    public SpecDataType getSpecDataType()
    {
        return specDataType;
    }

    public Tolerance getLeftParentMassTolerance()
    {
        return leftParentMassTolerance;
    }

    public Tolerance getRightParentMassTolerance()
    {
        return rightParentMassTolerance;
    }

    //	public int getNumAllowedC13()								{ return numAllowedC13; }
    public int getMaxIsotopeError()
    {
        return maxIsotopeError;
    }

    public int getMinIsotopeError()
    {
        return minIsotopeError;
    }

    public List<SpecKey> getSpecKeyList()
    {
        return specKeyList;
    }

    public SpecKey getSpecKey(int specIndex, int charge)
    {
        return specIndexChargeToSpecKeyMap.get(new Pair<Integer, Integer>(specIndex, charge));
    }

    public NewRankScorer getRankScorer(SpecKey specKey)
    {
        if (specKeyRankScorerMap == null)
            return null;
        else
            return this.specKeyRankScorerMap.get(specKey);
    }

//	public Tolerance getSpectrumSpecificPrecursorTolerance(SpecKey specKey)
//	{
//		if(specKeyToleranceMap == null)
//			return null;
//		else
//			return specKeyToleranceMap.get(specKey);
//	}

    public ScoredSpectraMap makePepMassSpecKeyMap()
    {
        for (SpecKey specKey : specKeyList) {
            int specIndex = specKey.getSpecIndex();
            Spectrum spec = specAcc.getSpectrumBySpecIndex(specIndex);
            float peptideMass = (spec.getPrecursorPeak().getMz() - (float) Composition.ChargeCarrierMass()) * specKey.getCharge() - (float) Composition.H2O;

            for (int delta = this.minIsotopeError; delta <= maxIsotopeError; delta++) {
                float mass1 = peptideMass - delta * (float) Composition.ISOTOPE;
                double mass1Key = (double) mass1;
                while (pepMassSpecKeyMap.get(mass1Key) != null)
                    mass1Key = Math.nextUp(mass1Key);
                pepMassSpecKeyMap.put(mass1Key, specKey);
            }
            specIndexChargeToSpecKeyMap.put(new Pair<Integer, Integer>(specIndex, specKey.getCharge()), specKey);
//			if(specKeyToleranceMap != null && spec.getPrecursorTolerance() != null)
//				specKeyToleranceMap.put(specKey, spec.getPrecursorTolerance());
        }
        return this;
    }

    public ProgressData getProgressObj()
    {
        return progress;
    }

    public void setProgressObj(ProgressData progObj)
    {
        progress = progObj;
    }

    public void preProcessSpectra()
    {
        preProcessSpectra(0, specKeyList.size());
    }

    public void preProcessSpectra(int fromIndex, int toIndex)
    {
        if (progress == null) {
            progress = new ProgressData();
        }
        if (specDataType.getActivationMethod() != ActivationMethod.FUSION)
            preProcessIndividualSpectra(fromIndex, toIndex);
        else
            preProcessFusedSpectra(fromIndex, toIndex);
    }

    private void preProcessIndividualSpectra(int fromIndex, int toIndex)
    {
        NewRankScorer scorer = null;
        ActivationMethod activationMethod = specDataType.getActivationMethod();
        InstrumentType instType = specDataType.getInstrumentType();
        Enzyme enzyme = specDataType.getEnzyme();
        Protocol protocol = specDataType.getProtocol();

        if (activationMethod != ActivationMethod.ASWRITTEN && activationMethod != ActivationMethod.FUSION) {
            scorer = NewScorerFactory.get(activationMethod, instType, enzyme, protocol);
            if (this.turnOffEdgeScoring)
                scorer.doNotUseError();
        }
        int count = 0;
        int total = toIndex - fromIndex;
        for (SpecKey specKey : specKeyList.subList(fromIndex, toIndex)) {
            if (Thread.currentThread().isInterrupted()) {
                return;
            }

            int specIndex = specKey.getSpecIndex();
            Spectrum spec = specAcc.getSpectrumBySpecIndex(specIndex);
            if (activationMethod == ActivationMethod.ASWRITTEN || activationMethod == ActivationMethod.FUSION) {
                scorer = NewScorerFactory.get(spec.getActivationMethod(), instType, enzyme, protocol);
                if (this.turnOffEdgeScoring)
                    scorer.doNotUseError();
            }
            int charge = specKey.getCharge();
            spec.setCharge(charge);

            // System.out.println("GetScoredSpectrum for " + specKey.toString());
            NewScoredSpectrum<NominalMass> scoredSpec = scorer.getScoredSpectrum(spec);

            float peptideMass = spec.getParentMass() - (float) Composition.H2O;
            float tolDaLeft = leftParentMassTolerance.getToleranceAsDa(peptideMass);
            int maxNominalPeptideMass = NominalMass.toNominalMass(peptideMass) + Math.round(tolDaLeft - 0.4999f) - this.minIsotopeError;

            if (scorer.supportEdgeScores()) {
                specKeyScorerMap.put(specKey, new DBScanScorer(scoredSpec, maxNominalPeptideMass));
            } else {
                specKeyScorerMap.put(specKey, new FastScorer(scoredSpec, maxNominalPeptideMass));
            }

            if (specKeyRankScorerMap != null) {
                specKeyRankScorerMap.put(specKey, scorer);
            }

            count++;
            progress.report(count, total);
        }
    }

    private void preProcessFusedSpectra(int fromIndex, int toIndex)
    {
        InstrumentType instType = specDataType.getInstrumentType();
        Enzyme enzyme = specDataType.getEnzyme();
        Protocol protocol = specDataType.getProtocol();

        for (SpecKey specKey : specKeyList.subList(fromIndex, toIndex)) {
            if (Thread.currentThread().isInterrupted()) {
                return;
            }

            ArrayList<Integer> specIndexList = specKey.getSpecIndexList();
            if (specIndexList == null) {
                specIndexList = new ArrayList<Integer>();
                specIndexList.add(specKey.getSpecIndex());
            }
            ArrayList<ScoredSpectrum<NominalMass>> scoredSpecList = new ArrayList<ScoredSpectrum<NominalMass>>();
            boolean supportEdgeScore = true;
            for (int specIndex : specIndexList) {
                if (Thread.currentThread().isInterrupted()) {
                    return;
                }

                Spectrum spec = specAcc.getSpectrumBySpecIndex(specIndex);

                NewRankScorer scorer = NewScorerFactory.get(spec.getActivationMethod(), instType, enzyme, protocol);
                if (!scorer.supportEdgeScores())
                    supportEdgeScore = false;
                int charge = specKey.getCharge();
                spec.setCharge(charge);
                NewScoredSpectrum<NominalMass> sSpec = scorer.getScoredSpectrum(spec);
                scoredSpecList.add(sSpec);
            }

            if (scoredSpecList.size() == 0)
                continue;
            ScoredSpectrumSum<NominalMass> scoredSpec = new ScoredSpectrumSum<NominalMass>(scoredSpecList);
            float peptideMass = scoredSpec.getPrecursorPeak().getMass() - (float) Composition.H2O;
            float tolDaLeft = leftParentMassTolerance.getToleranceAsDa(peptideMass);
            int maxNominalPeptideMass = NominalMass.toNominalMass(peptideMass) + Math.round(tolDaLeft - 0.4999f) + 1;
            if (supportEdgeScore)
//				specKeyScorerMap.put(specKey, new DBScanScorerSum(scoredSpecList, maxNominalPeptideMass));
                specKeyScorerMap.put(specKey, new FastScorer(scoredSpec, maxNominalPeptideMass));
            else
                specKeyScorerMap.put(specKey, new FastScorer(scoredSpec, maxNominalPeptideMass));
        }
    }
}
