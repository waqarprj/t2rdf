
package twittertordf;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.*;
import javax.xml.parsers.DocumentBuilder.*;
import java.net.*;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.sparql.util.graph.GraphUtils;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.w3c.dom.*;

/**
 * Twitter to RDF REST search service
 *
 * @author Waqar
 */
@WebServlet(name = "SearchService", urlPatterns = {"/search"})
public class SearchService extends HttpServlet {

    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        PrintWriter out = response.getWriter();
        String outputFormat = "rdfa";
        Model model;


        try {

            if (request.getParameter("f") != null) outputFormat = request.getParameter("f");
    
            //gets the complete query
            String query = getQuery(request);
            
            //creates model in memory 
            model = createModel();
            
            //loads the data from twitter  into the model
            loadTwitterData(model, query);
            
            //outputs the RDF in the appropriate format
            if (outputFormat.equalsIgnoreCase("rdf")) {
                response.setContentType("application/rdf+xml;charset=UTF-8");
                response.setHeader("Content-disposition","attachment; filename=\"" +request.getParameter("q") +".rdf\"");
                model.write(out, "RDF/XML-ABBREV"); 
            } else if (outputFormat.equalsIgnoreCase("n3")) {
                response.setContentType("text/n3;charset=UTF-8");
                response.setHeader("Content-disposition","attachment; filename=\"" +request.getParameter("q") +".n3\"");
                model.write(out, "N3");     
            }else if (outputFormat.equalsIgnoreCase("n-triple")) {
                response.setContentType("text/plain;charset=UTF-8");
                response.setHeader("Content-disposition","attachment; filename=\"" +request.getParameter("q") +".nt\"");
                model.write(out, "N-TRIPLE");
             
            }
            else if (outputFormat.equalsIgnoreCase("turtle")) {
                response.setContentType("application/x-turtle;charset=UTF-8");
                response.setHeader("Content-disposition","attachment; filename=\"" +request.getParameter("q") +".ttl\"");
                model.write(out, "TURTLE");
            }
            else {
                //outputs a RDFa page by default
                response.setContentType("text/html;charset=UTF-8");
                writeRDFa(model, out);
            }
        } catch (Exception e) {
            //displys an error
            writeError(out, e);
        } finally {
            out.close();
        }
    }

   /**
    * Get and controls the query string 
    * @param request httprequest object
    * @throws Exception 
    * 
    */
    protected String getQuery(HttpServletRequest request) throws Exception{
        String query;
        if (request.getParameter("q") != null) query = request.getParameter("q");
        else throw new Exception("q parameter is missing the request");
        if (request.getParameter("n") != null)  query = query + "&rpp=" + request.getParameter("n");
        else query = query + "&rpp=25";
        if (request.getParameter("l") != null) query = query + "&lang=" + request.getParameter("l");
        if (request.getParameter("g") != null) query = query + "&geocode=" + request.getParameter("g");
        if (request.getParameter("p") != null) query = query + "&page=" + request.getParameter("p");
        if (request.getParameter("t") != null) query = query + "&result_type=" + request.getParameter("t");   
        return query;
    }

    /**
     * Outputs the model as RDFa 
     * @param model model to to be outputted 
     * @param out printwritter for the response 
     * 
     */
    
    protected void writeRDFa(Model model, PrintWriter out) {
        
        //html header
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Servlet SearchService</title>");
        out.println(" <LINK href=\"style.css\" rel=\"stylesheet\" type=\"text/css\">");
        out.println("</head>");
        out.println("<body>");
      
        //get all the subjects
        ResIterator iterator = model.listSubjectsWithProperty(RDF.type, SIOCTypes.MicroblogPost);
      
        //renders all the tweets in HTML 
        while (iterator.hasNext()) {
            
            Resource resource = iterator.nextResource();
            out.println("<div class=\"box green\">");
            out.println("<div typeof=\"sioc:Post\" about=\"" + resource.getURI() + "\">");
            out.println("<img src=\"" + resource.getPropertyResourceValue(SIOC.has_creator).getProperty(SIOC.avatar).getLiteral() + "\"></img>");
            out.println("<b>" + resource.getProperty(FOAF.name).getLiteral().getValue() + "</b><br>");
            out.println("<i><span property=\"dcterms:created\">" + resource.getProperty(DCTerms.created).getLiteral().getValue() + "</span></i>");
            out.println("<div property=\"sioc:content\" xml:lang=\"" + resource.getProperty(SIOC.content).getLiteral().getLanguage() + "\"><rdf:value>" + resource.getProperty(SIOC.content).getLiteral().getValue() + "</rdf:value></div>");
            out.println("<strong>Links</strong>");
            List<RDFNode> links= GraphUtils.multiValue(resource, SIOC.links_to);
            for(RDFNode link : links)out.println("<br/>&gt <span property=\"dcterms:linksto\">" + link.toString() + "</span>");;
            links= GraphUtils.multiValue(resource, SIOC.addressed_to);
            for(RDFNode link : links)out.println("<br/>@ <span property=\"dcterms:addressed_to\">" + link.toString() + "</span>");;
            links= GraphUtils.multiValue(resource, SIOC.has_topic);
            for(RDFNode link : links)out.println("<br/># <span property=\"dcterms:has_topic\">" + link.toString() + "</span>");;
            out.println("</div></div>");
        }
        
        //renders the footer
        out.println("<div class=\"box\"><a  href=\"http://validator.w3.org/check?uri=referer\"><img src=\"http://www.w3.org/Icons/valid-xhtml-rdfa-blue\" alt=\"'Valid XHTML + RDFa' button\"   /></a><div>");
        out.println("</body>");
        out.println("</html>");
    }

    /**
     * Error page
     * @param out printwriter for response 
     * @param e exception 
     * 
     */
    protected void writeError(PrintWriter out, Exception e) {
        out.println("<html>");
        out.println("<head>");
        out.println("<title>SearchService Error</title>");
        out.println(" <LINK href=\"style.css\" rel=\"stylesheet\" type=\"text/css\">");
        out.println("</head>");
        out.println("<body><div class=\"box red\">");
        out.println("<h1>Error</h1>");
        out.println("<h2>" + e.toString()+"</h2>");
        e.printStackTrace(out);
        out.println("</div></body>");
        out.println("</html>");
    }
    
    /**
     * creates the model in memory and sets the prefixes
     *
     */
    protected Model createModel() {
        Model model = ModelFactory.createDefaultModel();
        model.setNsPrefix("sioc", SIOC.getURI());
        model.setNsPrefix("sioctypes", SIOCTypes.getURI());
        model.setNsPrefix("dcterms", DCTerms.getURI());
        model.setNsPrefix("foaf", FOAF.getURI());
        return model;
    }

    /**
     * loads data from twitter
     * @param model model for loading the data
     * @param query query
     * @throws Exception
     * 
     */
    protected void loadTwitterData(Model model, String query) throws Exception {

        //connects to twitter.com and invokes the search service 
        URL url = new URL("http://search.twitter.com/search.atom?q=" + query);
        URLConnection conn = url.openConnection();

        //builds XML DOM from the response 
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(conn.getInputStream());
        
        //patterns regix 
        Pattern pattern_addressto = Pattern.compile("(^|\\s+)@(\\w+)");
        Pattern pattern_linksto = Pattern.compile("[A-Za-z]+:\\/\\/[A-Za-z0-9-_]+\\.[A-Za-z0-9-_:%&~\\?\\/.=]+");
        Pattern pattern_hastopic = Pattern.compile("(^|\\s+)#(\\w+)");
 

        //gets all the entries 
        NodeList entries = document.getDocumentElement().getElementsByTagName("entry");

       String resource_uri,avatar, content, title,lang, created, has_topic, user,user_uri;
        
        //loads all the entries into model
        for (int i = 0; i < entries.getLength(); i++) {
            
            //entry
            Element entry = (Element) entries.item(i);
            
            resource_uri = entry.getElementsByTagName("link").item(0).getAttributes().getNamedItem("href").getNodeValue();
            avatar = entry.getElementsByTagName("link").item(1).getAttributes().getNamedItem("href").getNodeValue();
            created = entry.getElementsByTagName("published").item(0).getChildNodes().item(0).getNodeValue();
            has_topic = "http://twitter.com/search?q=" + query.split("&")[0];
            user = entry.getElementsByTagName("author").item(0).getChildNodes().item(0).getChildNodes().item(0).getNodeValue();
            user_uri =entry.getElementsByTagName("author").item(0).getChildNodes().item(1).getChildNodes().item(0).getNodeValue()+"#me";
            content = entry.getElementsByTagName("content").item(0).getChildNodes().item(0).getTextContent();
            title = entry.getElementsByTagName("title").item(0).getChildNodes().item(0).getTextContent();
            lang =entry.getElementsByTagName("twitter:lang").item(0).getChildNodes().item(0).getNodeValue();
            
            //creats the resource
            Resource resource = model.createResource(resource_uri);
            
            //adding properties
            resource.addProperty(RDF.type, SIOCTypes.MicroblogPost);
            resource.addProperty(DCTerms.created, created);
            resource.addProperty(DCTerms.title,title);
            resource.addProperty(DCTerms.language, lang);
            resource.addProperty(SIOC.has_topic, has_topic);
            resource.addProperty(FOAF.name, user);
            Resource account = model.createResource(user_uri);
            account.addProperty(RDF.type, SIOC.UserAccount);
            account.addProperty(SIOC.avatar, avatar);
            resource.addProperty(SIOC.has_creator,account);
            resource.addProperty(SIOC.content, model.createLiteral(content, lang));
            Matcher matcher = pattern_addressto.matcher(title);
            while(matcher.find())resource.addProperty(SIOC.addressed_to, "http://www.twitter.com/"+matcher.group().trim().substring(1)+"#me");
            matcher = pattern_linksto.matcher(title);
            while(matcher.find())resource.addProperty(SIOC.links_to, matcher.group());
            matcher = pattern_hastopic.matcher(title);
            while(matcher.find())resource.addProperty(SIOC.has_topic, "http://twitter.com/search?q="+matcher.group().trim().substring(1));
            
        }

    }

    

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
