package com.suprtek.taxiiclients;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.mitre.taxii.client.HttpClient;
import org.mitre.taxii.messages.xml11.CollectionInformationRequest;
import org.mitre.taxii.messages.xml11.CollectionInformationResponse;
import org.mitre.taxii.messages.xml11.MessageHelper;
import org.mitre.taxii.messages.xml11.StatusMessage;

/**
 *
 * @author jruhnke
 */
public class CollectionInformationClient extends AbstractClient {

    public static void main(String[] args) {
        // Create a client that uses basic authentication (user & password).
        HttpClientBuilder cb = HttpClientBuilder.create();
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(
                AuthScope.ANY,
                new UsernamePasswordCredentials("guest", "guest"));
        cb.setDefaultCredentialsProvider(credsProvider);
        CloseableHttpClient httpClient = cb.build();

        // Create a Taxii Client with the HttpClient object.
        HttpClient taxiiClient = new HttpClient(httpClient);

        // Prepare the message to send.
        CollectionInformationRequest request = factory.createCollectionInformationRequest().withMessageId(MessageHelper.generateMessageId());

        try {
            Object responseObj = taxiiClient.callTaxiiService(new URI(DATA_URL), request);

            if (responseObj instanceof CollectionInformationResponse) {
                CollectionInformationResponse ciResp = (CollectionInformationResponse) responseObj;
                processCollectionInformationResponse(ciResp);
            } else if (responseObj instanceof StatusMessage) {
                StatusMessage sm = (StatusMessage) responseObj;
                processStatusMessage(sm);
            }
        } catch (URISyntaxException | JAXBException | IOException ex) {
            Logger.getLogger(CollectionInformationClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void processStatusMessage(StatusMessage sm) {
        printTaxiiXml(sm);
    }

    private static void processCollectionInformationResponse(CollectionInformationResponse ciResp) {
        printTaxiiXml(ciResp);
    }

}
