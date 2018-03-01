package ruhnke.taxiiclients;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import org.mitre.taxii.messages.TaxiiXml;
import org.mitre.taxii.messages.xml11.ObjectFactory;
import org.mitre.taxii.messages.xml11.TaxiiXmlFactory;

/**
 *
 * @author jruhnke
 */
abstract class AbstractClient {

    public static final String DISCOVERY_URL = "http://hailataxii.com/taxii-discovery-service";
    public static final String DATA_URL = "http://hailataxii.com/taxii-data";
    public static ObjectFactory factory = new ObjectFactory();
    static TaxiiXmlFactory txf = new TaxiiXmlFactory();
    public static TaxiiXml taxiiXml;

    public static void printTaxiiXml(Object obj) {
        taxiiXml = txf.createTaxiiXml();
        try {
            String xmlString = taxiiXml.marshalToString(obj, true);
            System.out.println(xmlString);
        } catch (JAXBException ex) {
            Logger.getLogger(AbstractClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
