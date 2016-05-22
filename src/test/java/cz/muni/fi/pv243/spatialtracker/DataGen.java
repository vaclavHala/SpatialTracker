package cz.muni.fi.pv243.spatialtracker;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.io.*;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.util.regex.Pattern.compile;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * Give it template for json-generator ({@code beta.json-generator.com}).
 * It will give you random JSON generated from that template.
 * Internet connection needs to be available and the json-gen service needs to be up.
 * <p>
 * Example template (see help at json-gen site):<pre>
 * {
 *   "login" : "{{lorem(1, 'words')}}",
 *   "password" : "{{lorem(1, 'words')}}",
 *   "email": "{{firstName()}}@{{surname()}}.{{random('cz','sk','com','org')}}"
 * }
 * </pre>
 */
public class DataGen {

    private static final String GEN_API_URL = "http://beta.json-generator.com/api/templates";
    private static final Pattern GRAB_XSRF_TOKEN = compile("XSRF-TOKEN=([^;]+);");

    public static String generateFrom(InputStream templateStream){
        StringBuilder template = new StringBuilder();
        new BufferedReader(new InputStreamReader(templateStream)).lines().forEach(line -> template.append(line));
        return generateFrom(template.toString());
    }

    public static String generateFrom(String template) {
        try {
            String body = formatBody(template);
            String xsrfToken = getXsrfToken();
            return generate(body, xsrfToken);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String getXsrfToken() throws UnirestException, UnsupportedEncodingException {
        HttpResponse<String> resp = Unirest.head(GEN_API_URL).asString();
        return decodeToken(resp.getHeaders().getFirst("Set-Cookie"));
    }

    private static String decodeToken(String rawSetCookieHeader) throws UnsupportedEncodingException {
        Matcher m = GRAB_XSRF_TOKEN.matcher(rawSetCookieHeader);
        m.find();
        String rawToken = m.group(1);
        return new URLDecoder().decode(rawToken, "UTF-8");
    }

    private static String formatBody(String from) {
        String escaped = from.replace("\"", "\\\"")
                             .replace('\n', ' ')
                             .replace('\r', ' ');
        return String.format("{\"body\":\"%s\"}", escaped);
    }

    private static String generate(String formattedBody, String xsrfToken) throws UnirestException {
        return Unirest.post("http://beta.json-generator.com/api/templates")
                      .header("X-XSRF-TOKEN", xsrfToken)
                      .header(CONTENT_TYPE, APPLICATION_JSON)
                      .body(formattedBody)
                      .asJson().getBody().getObject()
                      .getJSONObject("generatedJSON").toString();
    }

}
