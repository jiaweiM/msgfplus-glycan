package omics.msgf.mzml;

import omics.msgf.msutil.Spectrum;
import uk.ac.ebi.jmzml.xml.io.MzMLObjectIterator;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshaller;

import java.io.File;
import java.util.Iterator;


public class MzMLSpectraIterator implements Iterator<Spectrum>, Iterable<Spectrum>
{
    private final MzMLUnmarshaller unmarshaller;
    private final int minMSLevel;        // inclusive
    private final int maxMSLevel;        // exclusive

    private MzMLObjectIterator<uk.ac.ebi.jmzml.model.mzml.Spectrum> itr;
    private boolean hasNext;
    private Spectrum currentSpectrum = null;

    public MzMLSpectraIterator(MzMLAdapter mzmlAdapter)
    {
        unmarshaller = mzmlAdapter.getUnmarshaller();
        minMSLevel = mzmlAdapter.getMinMSLevel();
        maxMSLevel = mzmlAdapter.getMaxMSLevel();

        itr = unmarshaller.unmarshalCollectionFromXpath("/run/spectrumList/spectrum", uk.ac.ebi.jmzml.model.mzml.Spectrum.class);
        currentSpectrum = parseNextSpectrum();
        hasNext = currentSpectrum != null;
    }

    public static void main(String argv[]) throws Exception
    {
//		List<Logger> loggers = Collections.<Logger>list(LogManager.getCurrentLoggers());
//		loggers.add(LogManager.getRootLogger());
//		for ( Logger logger : loggers ) {
//		    logger.setLevel(Level.OFF);
//		}
        test();
    }

    public static void test() throws Exception
    {
        File xmlFile = new File("/cygwin/home/kims336/Research/Data/JMzReader/example.mzML");
        xmlFile = new File("/cygwin/home/kims336/Research/Data/JMzReader/small.pwiz.1.1.mzML");

        MzMLAdapter adapter = new MzMLAdapter(xmlFile);
        MzMLSpectraIterator itr = new MzMLSpectraIterator(adapter);
        while (itr.hasNext()) {
            Spectrum spec = itr.next();
            System.out.println("-----------");
            System.out.println(spec.getID());
            System.out.println(spec.getSpecIndex());
            System.out.println(spec.getMSLevel());
            if (spec.getMSLevel() == 2) {
                System.out.println(spec.getPrecursorPeak().getMz());
                System.out.println(spec.getPrecursorPeak().getCharge());
                System.out.println(spec.getActivationMethod().getName());
            }
        }
    }

    public boolean hasNext()
    {
        return hasNext;
    }

    /**
     * Get next spectrum.
     *
     * @return the next spectrum.
     */
    public Spectrum next()
    {
        Spectrum curSpecCopy = currentSpectrum;
        currentSpectrum = parseNextSpectrum();
        if (currentSpectrum == null)
            hasNext = false;
        return curSpecCopy;
    }

    public Spectrum parseNextSpectrum()
    {
        Spectrum spec = null;
        uk.ac.ebi.jmzml.model.mzml.Spectrum jmzSpec = null;

        while (itr.hasNext()) {
            jmzSpec = itr.next();
            spec = SpectrumConverter.getSpectrumFromJMzMLSpec(jmzSpec);
            if (spec.getMSLevel() < minMSLevel || spec.getMSLevel() > maxMSLevel)
                continue;
            else
                return spec;
        }

        return null;
    }

    public void remove()
    {
        throw new UnsupportedOperationException("SpectraIterator.remove() not implemented");
    }

    public Iterator<Spectrum> iterator()
    {
        return this;
    }

}