import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpClient {

    private String host;
    private final List<RequestLog> logs = new ArrayList<>();
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private long totalRequests = 0;
    private long successfulRequests = 0;
    private long failedRequests = 0;

    public HttpClient(String host) {
        this.host = host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String postRequest(String endpoint, String jsonPayload) throws IOException {
        long startTime = System.currentTimeMillis();
        URL url = new URL(host + endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; utf-8");
        conn.setRequestProperty("Accept", "application/json");
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int responseCode = conn.getResponseCode();
        StringBuilder response = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
        }

        logRequest("POST", endpoint, responseCode, response.toString(), System.currentTimeMillis() - startTime);
        updateStatistics(responseCode);

        conn.disconnect();
        return response.toString();
    }

    public void postRequestAsync(String endpoint, String jsonPayload, Callback callback) {
        executor.submit(() -> {
            try {
                String response = postRequest(endpoint, jsonPayload);
                callback.onSuccess(response);
            } catch (IOException e) {
                callback.onError(e);
            }
        });
    }

    public List<RequestLog> getLogs() {
        return new ArrayList<>(logs);
    }

    public long getTotalRequests() {
        return totalRequests;
    }

    public long getSuccessfulRequests() {
        return successfulRequests;
    }

    public long getFailedRequests() {
        return failedRequests;
    }

    public double getSuccessRate() {
        return totalRequests == 0 ? 0 : (double) successfulRequests / totalRequests * 100;
    }

    private synchronized void logRequest(String method, String endpoint, int responseCode, String response, long duration) {
        if (logs.size() == 20) {
            logs.remove(0); // FIFO: Entferne die älteste Anfrage
        }
        logs.add(new RequestLog(method, endpoint, responseCode, response, duration));
    }

    private synchronized void updateStatistics(int responseCode) {
        totalRequests++;
        if (responseCode >= 200 && responseCode < 300) {
            successfulRequests++;
        } else {
            failedRequests++;
        }
    }

    public static class RequestLog {
        private final String method;
        private final String endpoint;
        private final int responseCode;
        private final String response;
        private final long duration;

        public RequestLog(String method, String endpoint, int responseCode, String response, long duration) {
            this.method = method;
            this.endpoint = endpoint;
            this.responseCode = responseCode;
            this.response = response;
            this.duration = duration;
        }

        @Override
        public String toString() {
            return String.format("Method: %s, Endpoint: %s, ResponseCode: %d, Duration: %dms, Response: %s",
                    method, endpoint, responseCode, duration, response);
        }
    }

    public interface Callback {
        void onSuccess(String response);
        void onError(Exception e);
    }
}
