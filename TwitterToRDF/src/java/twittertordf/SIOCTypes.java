package twittertordf;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 *
 * @author Waqar
 */
public class SIOCTypes {

    public static String getURI() {
        return uri;
    }
    protected static final String uri = "http://rdfs.org/sioc/types#";
    private static Model m = ModelFactory.createDefaultModel();
    public static final Resource MicroblogPost = m.createResource(uri + "MicroblogPost");
}
