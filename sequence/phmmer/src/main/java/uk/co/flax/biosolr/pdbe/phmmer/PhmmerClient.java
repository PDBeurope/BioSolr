package uk.co.flax.biosolr.pdbe.phmmer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import uk.co.flax.biosolr.pdbe.fasta.FastaJob;

public class PhmmerClient {

  private static final Logger LOG = LoggerFactory.getLogger(PhmmerClient.class);
  
  private String phmmerUrl;
  
  public PhmmerClient(String phmmerUrl) {
      this.phmmerUrl = phmmerUrl;
  }

  public JsonObject getResults(String database, String sequence) throws IOException {
    String submissionUrl = this.phmmerUrl + "/search/phmmer";

    // submit the job
    LOG.debug("submission URL=" + submissionUrl);
    try (HttpConnection http = new HttpConnection(submissionUrl)) {
      http.post("{" +
            "\"database\":\"" + database + "\"," +
            "\"input\":\">Seq\\n" + sequence + "\"" +
            "}");
      String jobId = http.getJson().getString("id");
      LOG.debug("Submitted job with ID=" + jobId);

      // get the results
      String resultsUrl = this.phmmerUrl + "/result/" + jobId +"?with_domains=true";
      LOG.debug("results URL=" + resultsUrl);

      try (HttpConnection http2 = new HttpConnection(resultsUrl)) {
        http2.get("application/json");

        LOG.debug("Response is " + http2.getResponseCode() + " " + http2.getResponseMessage());
        JsonObject response = http2.getJson();
        String status = response.getString("status");
        while (!status.equals("SUCCESS")) {
          LOG.debug("Job status is " + status + "; waiting 3s ...");
          try {
            Thread.sleep(3000);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }

          // handle failures
          if (status.equals("failure")) {
            LOG.error("Job failed: " +jobId);
            throw new IOException("Job failed: " + response.getString("message"));
          }

          try (HttpConnection httpRetry = new HttpConnection(resultsUrl)) {
            httpRetry.get("application/json");
            response = httpRetry.getJson();
            status = response.getString("status");
          }
        }

        return response;
      }
      
    }

  }
  
  private String getResultsUrl(String database, String sequence) throws IOException {
    LOG.debug("getting PHMMER data for seqdb=" + database + "; sequence=" + sequence);
    try (HttpConnection http = new HttpConnection(phmmerUrl)) {
      // http.post("seqdb=" + database + "&seq=>Seq%0D%0A" + sequence);
      http.post("{" +
            "\"database\":\"" + database + "\"," +
            "\"input\":\">Seq\\n" + sequence + "\"" +
            "}");
      return http.getJson().getString("id");
      // return http.getHeader("Location");
    }
  }

  private JsonObject getResultsJson(String url) throws IOException {
    try (HttpConnection http = new HttpConnection(url)) {
      http.get("application/json");
      return http.getJson();
    }
  }
  
  private class HttpConnection implements AutoCloseable {

    private HttpURLConnection http;
    
    private HttpConnection(String url) throws IOException {
      LOG.debug("opening connection to " + url);
      http = (HttpURLConnection)new URL(url).openConnection();
    }
    
    private void post(String params) throws IOException {
      LOG.debug("POSTing \"" + params + "\"");
      http.setRequestMethod("POST");
      http.setDoOutput(true);
      http.setInstanceFollowRedirects(false);
      http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
      http.setRequestProperty("Content-Length", Integer.toString(params.getBytes().length));
      try (OutputStream out = http.getOutputStream()) {
        out.write(params.getBytes());
      }
      
      LOG.debug("response is " + http.getResponseCode() + " " + http.getResponseMessage());
    }
    
    private String getHeader(String key) {
      return http.getHeaderField(key);
    }
    
    private void get(String accept) throws ProtocolException {
      http.setRequestMethod("GET");
      http.setRequestProperty("Accept", accept);
    }
    
    private JsonObject getJson() throws IOException {
      try (InputStream in = http.getInputStream();
           JsonReader reader = Json.createReader(in)) {
        return reader.readObject();
      }
    }

    private int getResponseCode() throws IOException {
      return http.getResponseCode();
    }

    private String getResponseMessage() throws IOException {
      return http.getResponseMessage();
    }
    
    @Override
    public void close() {
      http.disconnect();
    }
    
  }
  
}
