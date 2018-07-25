package omics.msgf.msdbsearch;

import com.google.common.io.Resources;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * @author JiaweiMao
 * @version 1.0.0
 * @since 23 Jun 2018, 10:46 AM
 */
public class CompactFastaSequenceTest
{

    @Test
    public void testGetRatioUniqueProteins()
    {
        CompactFastaSequence fastaSequence = new CompactFastaSequence(Resources.getResource("ecoli.fasta").getFile());
        fastaSequence.getRatioUniqueProteins();

    }
}