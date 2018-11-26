package com.quicktutorialz.microservices.TodoMicroservice.proxy;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Service
public class UserProxy {

    private static final Logger log = LoggerFactory.getLogger(UserProxy.class);

    public String talkToUserMicroServiceToVerifyUserAndGetEmail(HttpServletRequest request) {
        DefaultHttpClient httpclient = new DefaultHttpClient();
        String email = null;
        try {
            String urls = "http://localhost:8382/verifyJwtAndGetData3";
            HttpPost httpPost = new HttpPost(urls);

            httpPost.addHeader("jwt", request.getHeader("jwt"));

            HttpResponse response = httpclient.execute(httpPost);
            if(response.getStatusLine().getStatusCode()==403){
                log.info(">>>> I talked to User-Microservice...but,it didn't work out.");
                return null;
            }
            log.info("==== response === " + response.toString());
            log.info("==== entitiy  === " + response.getEntity());

            Header[] emailHeader = response.getHeaders("email");
            email = emailHeader[0].getValue();

        }catch (IOException e){
            e.printStackTrace();
        }
        return email;
    }
}
