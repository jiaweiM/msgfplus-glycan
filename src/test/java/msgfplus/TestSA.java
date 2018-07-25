package msgfplus;

import java.io.File;
import java.net.URISyntaxException;

import omics.msgf.msdbsearch.SuffixArrayForMSGFDB;
import omics.msgf.msutil.Composition;
import org.junit.Ignore;
import org.junit.Test;

import omics.msgf.msdbsearch.CompactFastaSequence;
import omics.msgf.msdbsearch.DBScanner;
import omics.msgf.msgf.Tolerance;
import omics.msgf.msutil.AminoAcid;
import omics.msgf.msutil.AminoAcidSet;
import omics.msgf.suffixarray.SuffixArray;
import omics.msgf.suffixarray.SuffixArraySequence;

public class TestSA {

    @Test
    public void getAAProbabilities() throws URISyntaxException {
        File dbFile = new File(TestSA.class.getClassLoader().getResource("human-uniprot-contaminants.fasta").toURI());
        AminoAcidSet aaSet = AminoAcidSet.getStandardAminoAcidSetWithFixedCarbamidomethylatedCys();
        DBScanner.setAminoAcidProbabilities(dbFile.getPath(), aaSet);
        for(AminoAcid aa : aaSet)
        {
            System.out.println(aa.getResidue()+"\t"+aa.getProbability());
        }
    }
    
    @Test
    public void getNumCandidatePeptides() throws URISyntaxException {
        File dbFile = new File(TestSA.class.getClassLoader().getResource("human-uniprot-contaminants.fasta").toURI());
        SuffixArraySequence sequence = new SuffixArraySequence(dbFile.getPath());
        SuffixArray sa = new SuffixArray(sequence);
        AminoAcidSet aaSet = AminoAcidSet.getAminoAcidSetFromModFile(new File(TestSA.class.getClassLoader().getResource("Mods.txt").toURI()).getAbsolutePath());
        System.out.println("NumPeptides: " + sa.getNumCandidatePeptides(aaSet, 2364.981689453125f, new Tolerance(10, true)));
    }

    
    @Test
    @Ignore
    public void testRedundantProteins() throws URISyntaxException {
        File databaseFile = new File(TestSA.class.getClassLoader().getResource("ecoli-reversed.fasta").toURI());
        
        CompactFastaSequence fastaSequence = new CompactFastaSequence(databaseFile.getPath());
        float ratioUniqueProteins = fastaSequence.getRatioUniqueProteins();
        if(ratioUniqueProteins < 0.5f)
        {
            fastaSequence.printTooManyDuplicateSequencesMessage(databaseFile.getName(), "MS-GF+", ratioUniqueProteins);
            System.exit(-1);
        }
        
        float fractionDecoyProteins = fastaSequence.getFractionDecoyProteins();
        if(fractionDecoyProteins < 0.4f || fractionDecoyProteins > 0.6f)
        {
            System.err.println("Error while reading: " + databaseFile.getName() + " (fraction of decoy proteins: "+ fractionDecoyProteins+ ")");
            System.err.println("Delete " + databaseFile.getName() + " and run MS-GF+ again.");
            System.exit(-1);
        }
        
    }

    @Test
    public void testTSA() throws Exception {
        File dbFile = new File(TestSA.class.getClassLoader().getResource("human-uniprot-contaminants.fasta").toURI());
        SuffixArraySequence sequence = new SuffixArraySequence(dbFile.getPath());

        long time;
        System.out.println("SuffixArrayForMSGFDB");
        time = System.currentTimeMillis();
        SuffixArrayForMSGFDB sa2 = new SuffixArrayForMSGFDB(sequence);
        System.out.println("Time: " + (System.currentTimeMillis() - time));
        int numCandidates = sa2.getNumCandidatePeptides(AminoAcidSet.getStandardAminoAcidSetWithFixedCarbamidomethylatedCys(), (383.8754f - (float) Composition.ChargeCarrierMass()) * 3 - (float) Composition.H2O, new Tolerance(2.5f, false));
        System.out.println("NumCandidatePeptides: " + numCandidates);
        int length10 = sa2.getNumDistinctPeptides(10);
        System.out.println("NumUnique10: " + length10);
    }
    
}
