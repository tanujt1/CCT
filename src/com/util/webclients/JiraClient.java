package com.util.webclients;

import com.util.BambooProperties;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.internal.util.Base64;
import org.glassfish.jersey.media.sse.SseFeature;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * Created by rajendv3 on 27/06/2017.
 */
public class JiraClient {

    BambooProperties properties;

    public JiraClient() {
    }

    public JiraClient(BambooProperties properties){
        System.out.println("Initializing Jira client....");
        this.properties=properties;
    }

    private void addCommentToJira(Comment comment){

        String username = properties.getUserName();
        String password = properties.getPassword();
        String jiraId = properties.getJiraTaskId();

        String encodedStr = Base64.encodeAsString(username+":"+password);

        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("SSL");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        System.getProperty("https.protocols","SSL");

        TrustManager[] trustAllCerts = {new InsecureTrustManager()};
        try {
            sslContext.init(null,trustAllCerts,new java.security.SecureRandom());
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        HostnameVerifier allHostsValid = new InsecureHostNameVerifier();

        Client jiraClient =  ClientBuilder.newBuilder().sslContext(sslContext).hostnameVerifier(allHostsValid).build();

        HttpAuthenticationFeature feature = HttpAuthenticationFeature.basicBuilder().build();

        jiraClient.register(feature);
        jiraClient.register(SseFeature.class);

        WebTarget webResource = jiraClient.target(String.format("https://jira.service.anz/rest/api/2/issue/%s/comment",jiraId));

        Invocation.Builder invokationBuilder = webResource.request(MediaType.APPLICATION_JSON)
                .header("Authorization","Basic : "+encodedStr);

        Response response = invokationBuilder.post(Entity.entity(comment,MediaType.APPLICATION_JSON));

        if(response.getStatus() != 201){
            throw new RuntimeException(" Failed : HTTP error code :"+response.getStatus());
        }

        System.out.println("Comment posted successfully.....");
    }


    public void addComment(String preface, List<String> contents){

        StringBuilder builder = new StringBuilder();

        for (String comment : contents){
            builder.append(comment).append("\n\n");
        }

        addCommentToJira(new Comment(preface+"\n\n"+builder.toString()));
    }
}

class Comment{
    String body;

    Comment(String body){
        this.body = body;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return body;
    }

}