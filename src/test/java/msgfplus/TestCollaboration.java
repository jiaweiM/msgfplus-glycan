package msgfplus;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Ignore;
import org.junit.Test;

import omics.msgf.params.ParamManager;
import omics.msgf.ui.MSGFPlus;

@Ignore
public class TestCollaboration {

    @Test
    @Ignore
    public void testSujunLiIndiana()
    {
        File dir = new File("C:\\cygwin\\home\\kims336\\Data\\Sujun");

        File specFile = new File(dir.getPath()+File.separator+"scan22564.mgf");
        File dbFile = new File(dir.getPath()+File.separator+"scan22564.fasta");
        File modFile = new File(dir.getPath()+File.separator+"Mods.txt");
        String[] argv = {"-s", specFile.getPath(), "-d", dbFile.getPath(), "-t", "2.5Da", "-mod", modFile.getPath()
                }; 

        ParamManager paramManager = new ParamManager("MS-GF+", MSGFPlus.VERSION, MSGFPlus.RELEASE_DATE, "java -Xmx3500M -jar MSGFPlus.jar");
        paramManager.addMSGFPlusParams();
        
        String msg = paramManager.parseParams(argv);
        if(msg != null)
            System.out.println(msg);
        assertTrue(msg == null);
        
        assertTrue(MSGFPlus.runMSGFPlus(paramManager) == null);
    }
}
