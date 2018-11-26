package com.quicktutorialz.microservices.TodoMicroservice.utilities;

import io.jsonwebtoken.*;

import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;

import org.json.simple.parser.ParseException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * This class provides method in order to generate and validate JSON Web Tokens
 */
@Component
public class JwtUtils {
    private static final Logger log = LoggerFactory.getLogger(JwtUtils.class);
    /* USEFUL LINKS:
        https://stormpath.com/blog/jwt-java-create-verify
        https://stormpath.com/blog/beginners-guide-jwts-in-java
        https://github.com/jwtk/jjwt
    */

    /**
     *
     * @param email
     * @param name
     * @param date
     * @return
     * @throws java.io.UnsupportedEncodingException
     */
    public String generateJwt(String email, String name, Date date) throws java.io.UnsupportedEncodingException {

        String jwt = Jwts.builder()
                .setSubject(email)
                .setExpiration(date)
                .claim("name", name)
                .signWith(
                        SignatureAlgorithm.HS256,
                        "myPersonalSecretKey12345".getBytes("UTF-8")
                )
                .compact();

        return jwt;
    }

    /**
     *
     * @param jwt
     * @return
     * @throws java.io.UnsupportedEncodingException
     * @throws ExpiredJwtException
     */
    public Map<String, Object> jwt2Map(String jwt) throws java.io.UnsupportedEncodingException, ExpiredJwtException {

        Jws<Claims> claim = Jwts.parser()
                .setSigningKey("myPersonalSecretKey12345".getBytes("UTF-8"))
                .parseClaimsJws(jwt);

        String name = claim.getBody().get("name", String.class);

        Date expDate = claim.getBody().getExpiration();
        String email = claim.getBody().getSubject();

        Map<String, Object> userData = new HashMap<>();
        userData.put("email", email);
        userData.put("name", name);
        userData.put("exp_date", expDate.toString());

        Date now = new Date();
        if (now.after(expDate)) {
            throw new ExpiredJwtException(null, null, "Session expired!");
        }

        return userData;
    }


    /**
     * this method extracts the jwt from the header or the cookie in the Http request
     *
     * @param request
     * @return jwt
     */
    public String getJwtFromHttpRequest(HttpServletRequest request) {
        String jwt = null;
        if (request.getHeader("jwt") != null) {
            jwt = request.getHeader("jwt");     //token present in header
        } else if (request.getCookies() != null) {
            Cookie[] cookies = request.getCookies();   //token present in cookie
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("jwt")) {
                    jwt = cookie.getValue();
                }
            }
        }
        return jwt;
    }

    public String getUserEmailFromHttpRequest(HttpServletRequest request) throws ParseException {
        RestTemplate restTemplate = new RestTemplate();
        String jwt = request.getHeader("jwt");
        MultiValueMap<String,String> headers = new LinkedMultiValueMap<String,String>();
        headers.add("jwt",jwt);
        // attach to http request.
        log.info(">>>> Before I start this.");
        HttpEntity<?> request2 = new HttpEntity(String.class,headers);

        try {
            ResponseEntity<JsonResponseBody> responseEntity = restTemplate.exchange("http://localhost:8382/verifyJwtAndGetData3", HttpMethod.POST, request2, JsonResponseBody.class);
            String jsonStringIncludingEmail = responseEntity.getBody().getResponse().toString();

            log.info(">>>>responseEntity : "+responseEntity.toString());
            log.info(">>>>responseEntity Body : "+responseEntity.getBody().getResponse());
            log.info(">>>>type : "+responseEntity.getBody().getClass().getName());
            log.info(">>>>jsonStringIncludingEmail : "+jsonStringIncludingEmail);

            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(jsonStringIncludingEmail);
            log.info(">>>> json : "+json.toString());
            log.info(">>>> json[email] : "+json.get("email"));
            String email = (String) json.get("email");
            return email;
        }catch(HttpClientErrorException e){
            log.info("e.getMessage():"+e.getMessage());
            log.info("e.getStatusCode():"+e.getStatusCode());
            log.info("e.getResponseBodyAsString():"+e.getResponseBodyAsString());
            return "JWT TOKEN IS INVALID";
        }




    }

}