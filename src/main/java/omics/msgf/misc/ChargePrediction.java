package omics.msgf.misc;

import omics.msgf.msgf.DeNovoGraph;
import omics.msgf.msgf.FlexAminoAcidGraph;
import omics.msgf.msgf.GeneratingFunction;
import omics.msgf.msgf.NominalMass;
import omics.msgf.msscorer.NewRankScorer;
import omics.msgf.msscorer.NewScoredSpectrum;
import omics.msgf.msscorer.NewScorerFactory;
import omics.msgf.msutil.*;
import omics.msgf.parser.MgfSpectrumParser;

public class ChargePrediction
{
    public static void main(String argv[]) throws Exception
    {
        testChargePrediction();
    }

    public static void testChargePrediction() throws Exception
    {
        AminoAcidSet aaSet = AminoAcidSet.getStandardAminoAcidSetWithFixedCarbamidomethylatedCys();
        ActivationMethod method = ActivationMethod.CID;
        Enzyme enzyme = Enzyme.TRYPSIN;
        NewRankScorer scorer = NewScorerFactory.get(method, enzyme);

        String fileName = "/home/sangtaekim/Research/Data/HeckRevision/AnnotatedSpectra/CID_Tryp_Confident.mgf";
        SpectraIterator itr = new SpectraIterator(fileName, new MgfSpectrumParser());

        int numPredictions = 0;
        int numCorrect = 0;
        int minCharge = 2;
        int maxCharge = 4;
        while (itr.hasNext()) {
            Spectrum spec = itr.next();
            int charge = spec.getCharge();
            if (charge < minCharge || charge > maxCharge)
                continue;

            numPredictions++;
            System.out.print(spec.getAnnotationStr() + "\t" + charge);
            int bestScore = Integer.MIN_VALUE;
            int bestCharge = -1;
            for (int c = minCharge; c <= maxCharge; c++) {
                spec.setCharge(c);
                int nominalPeptideMass = NominalMass.toNominalMass(spec.getPeptideMass());
                NewScoredSpectrum<NominalMass> scoredSpec = scorer.getScoredSpectrum(spec);
                DeNovoGraph<NominalMass> graph = new FlexAminoAcidGraph(
                        aaSet,
                        nominalPeptideMass,
                        enzyme,
                        scoredSpec
                );

                GeneratingFunction<NominalMass> gf = new GeneratingFunction<NominalMass>(graph)
                        .doNotBacktrack()
                        .doNotCalcNumber()
                        .doNotCalcProb();
                gf.computeGeneratingFunction();
                System.out.print("\t" + (gf.getMaxScore() - 1));
                if (bestScore < gf.getMaxScore() - 1) {
                    bestScore = gf.getMaxScore() - 1;
                    bestCharge = c;
                }
            }
            if (charge == bestCharge) {
                System.out.println("\t1");
                numCorrect++;
            } else {
                System.out.println("\t0");
            }
        }
        System.out.println(numCorrect / (float) numPredictions);
    }
}
