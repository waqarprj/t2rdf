package twittertordf;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 *
 * @author Waqar
 */
public class SIOC {

    public static String getURI() {
        return uri;
    }
    protected static final String uri = "http://rdfs.org/sioc/ns#";
    private static Model m = ModelFactory.createDefaultModel();
    public static final Property content = m.createProperty(uri + "content");
    public static final Property has_creator = m.createProperty(uri + "has_creator");
    public static final Property has_topic = m.createProperty(uri + "has_topic");
    public static final Property links_to = m.createProperty(uri + "links_to");
    public static final Property addressed_to = m.createProperty(uri + "addressed_to");
    public static final Property avatar = m.createProperty(uri + "avatar");
    public static final Resource Post = m.createResource(uri + "Post");
    public static final Resource UserAccount = m.createResource(uri + "UserAccount");
}
