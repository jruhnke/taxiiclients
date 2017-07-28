package com.suprtek.taxiiclients;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.mitre.taxii.client.HttpClient;
import org.mitre.taxii.messages.xml11.MessageHelper;
import org.mitre.taxii.messages.xml11.PollRequest;
import org.mitre.taxii.messages.xml11.PollResponse;
import org.mitre.taxii.messages.xml11.StatusMessage;

/**
 *
 * @author jruhnke
 */
public class PollClient extends AbstractClient {

    public static void main(String[] args) throws DatatypeConfigurationException {
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
        PollRequest request = factory.createPollRequest()
                .withMessageId(MessageHelper.generateMessageId())
                .withCollectionName("guest.dataForLast_7daysOnly")
                .withPollParameters(factory.createPollParametersType());

        Calendar gc = GregorianCalendar.getInstance();
        gc.setTime(getMeYesterday());
        XMLGregorianCalendar beginTime = DatatypeFactory.newInstance().newXMLGregorianCalendar((GregorianCalendar) gc).normalize();
        beginTime.setFractionalSecond(null);
        request.setExclusiveBeginTimestamp(beginTime);

        ZonedDateTime zdt = ZonedDateTime.now();
        gc.setTime(java.util.Date.from(zdt.toInstant()));
        XMLGregorianCalendar endTime = DatatypeFactory.newInstance().newXMLGregorianCalendar((GregorianCalendar) gc).normalize();
        endTime.setFractionalSecond(null);
        request.setInclusiveEndTimestamp(endTime);

        try {
            Object responseObj = taxiiClient.callTaxiiService(new URI(DATA_URL), request);

            if (responseObj instanceof PollResponse) {
                PollResponse pollResp = (PollResponse) responseObj;
                processPollResponse(pollResp);
            } else if (responseObj instanceof StatusMessage) {
                StatusMessage sm = (StatusMessage) responseObj;
                processStatusMessage(sm);
            }
        } catch (URISyntaxException | JAXBException | IOException ex) {
            Logger.getLogger(PollClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void processStatusMessage(StatusMessage sm) {
        printTaxiiXml(sm);
    }

    private static void processPollResponse(PollResponse pollResp) {
        printTaxiiXml(pollResp);
    }

    private static Date getMeYesterday() {
        return new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
    }

}
