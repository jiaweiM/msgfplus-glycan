package omics.msgf.params;

import omics.msgf.ui.MSGFPlus;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * @author JiaweiMao
 * @version 1.0.0
 * @since 22 Jun 2018, 2:53 PM
 */
public class ParamManagerTest
{

    @Test
    public void testAddParameter()
    {
    }

    @Test
    public void testAddExample()
    {
    }

    @Test
    public void testGetParameter()
    {
    }

    @Test
    public void testIsValid()
    {
    }

    @Test
    public void testPrintToolInfo()
    {
    }

    @Test
    public void testPrintUsageInfo()
    {
    }

    @Test
    public void testPrintValues()
    {
    }

    @Test
    public void testParseParams()
    {
    }

    @Test
    public void testAddSpecFileParam()
    {
    }

    @Test
    public void testAddDBFileParam()
    {
    }

    @Test
    public void testAddDBFileParam1()
    {
    }

    @Test
    public void testAddPMTolParam()
    {
    }

    @Test
    public void testAddMzIdOutputFileParam()
    {
    }

    @Test
    public void testAddOutputFileParam()
    {
    }

    @Test
    public void testAddFragMethodParam()
    {
    }

    @Test
    public void testAddFragMethodParam1()
    {
    }

    @Test
    public void testAddInstTypeParam()
    {
    }

    @Test
    public void testAddInstTypeParam1()
    {
    }

    @Test
    public void testAddEnzymeParam()
    {
    }

    @Test
    public void testAddEnzymeParam1()
    {
    }

    @Test
    public void testAddProtocolParam()
    {
    }

    @Test
    public void testAddProtocolParam1()
    {
    }

    @Test
    public void testAddModFileParam()
    {
    }

    @Test
    public void testAddMSGFPlusParams()
    {
        ParamManager paramManager = new ParamManager("MS-GF+", MSGFPlus.VERSION, MSGFPlus.RELEASE_DATE, "java -Xmx3500M -jar MSGFPlus.jar");
        paramManager.addMSGFPlusParams();



    }

    @Test
    public void testAddScoringParamGenParams()
    {
    }

    @Test
    public void testAddMSGFDBParams()
    {
    }

    @Test
    public void testAddMSGFParams()
    {
    }

    @Test
    public void testAddMSGFLibParams()
    {
    }

    @Test
    public void testGetSpecFileParam()
    {
    }

    @Test
    public void testGetDBFileParam()
    {
    }

    @Test
    public void testGetPMTolParam()
    {
    }

    @Test
    public void testGetOutputFileParam()
    {
    }

    @Test
    public void testGetActivationMethod()
    {
    }

    @Test
    public void testGetInstType()
    {
    }

    @Test
    public void testGetEnzyme()
    {
    }

    @Test
    public void testGetProtocol()
    {
    }

    @Test
    public void testGetModFileParam()
    {
    }

    @Test
    public void testGetIntValue()
    {
    }

    @Test
    public void testGetFloatValue()
    {
    }

    @Test
    public void testGetDoubleValue()
    {
    }

    @Test
    public void testGetFile()
    {
    }

    @Test
    public void testGetFiles()
    {
    }
}