<%-- 
    Document   : index
    Created on : Jul 29, 2012, 1:17:36 PM
    Author     : Waqar
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>TwitterToRDF</title>
        <LINK href="style.css" rel="stylesheet" type="text/css">
    </head>
    
    <body>
        <div class="box green">
        <h1>TwitterToRDF Service</h1>
        <p>
            TwitterToRDF service retrieves all the tweets matching the query for brand name in well-known RDF formats
            <br/><br/>
            <b> Examples:</b><br/>
            <a href="search?q=@pepsi">search?q=@pepsi<a> <br/>
            Returns results in RDFa
            <br/><br/>
            <a href="search?q=@CocaCola&f=rdf">search?q=@CocaCola&f=rdf<a> <br/>
            Return results in RDF/XML format
            
        </p>
                <strong>Test service</strong>
        <form action="search" method="get">
            Query : <input type="text" name="q" value="@PEPSI"><br/>
            Output format (RDFa by default): <br/>
            <input type="radio" name="f" value="rdf"/> RDF/XML<br/>
            <input type="radio" name="f" value="n3"/> N3<br>
            <input type="radio" name="f" value="turtle"/> Turtle<br/>
            <input type="radio" name="f" value="N-TRIPLE"/> N-TRIPLE<br/>
            <input type="submit" value="Query"/><br/>
        </form>
                
        <p>
            <b>GET search</b></p>
            <p>
            <b>Resource URL</b><br />
            http://hostaddress/TwitterToRDF/search
        </p>
    
        <table class="table">
        
        <tr>
            <td class="title">
                Parameter</td>
            <td class="title">
                Description</td>
        </tr>
        
        <tr>
            <td >
                q</td>
            <td>
                Brand name or related query<br />
                <strong>Example:</strong><br />
                PEPSI</td>
        </tr>
        
        <tr>
            <td >
                f</td>
            <td >
                [option] Output format for RDF by default returns RDFa.<br>
                <strong>Example Values:</strong><br />
                RDF, N3, N-TRIPLE or turtle. 
            </td>
        </tr>
        
        <tr>
            <td >
                n</td>
            <td >
                [option] Number of tweets per page. 100 is the maximum value for the n</td>
        </tr>
        
        <tr>
            <td >
                p</td>
            <td >
                [option] Page number. 1500 is the maximum value for the p</td>
        </tr>
        
        <tr>
            <td >
                l</td>
            <td >
                [option] Returns tweets in specified language only.</td>
             </tr>
        
        <tr>
            <td >
                r</td>
            <td >
                [option] Specifies what type of results to be fetched <br />
                <strong>Example Values:</strong><br />
                mixed, recent, popular
            </td>
        </tr>
        
        <tr>
            <td >
                g</td>
            <td >
                [option] returns the tweets within the specified geographical local.<br>
                <strong>Example Value:</strong><br />
                38.781157,-160.398720,3mi
             </td>
        </tr>
        
    </table>
        <br/>

        </div>
    </body>
</html>
