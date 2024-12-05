import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

class LanguageModelClient {

    private final HttpClient httpClient;
    private Model model;

    public enum Model {
        TINY_LLAMA("tinyllama"),
        LLAMA3_1("llama3.1"),
        LLAMA3_2_3B("llama3.2:3b"),
        MOONDREAM("moondream");

        private final String modelName;

        Model(String modelName) {
            this.modelName = modelName;
        }

        public String getModelName() {
            return modelName;
        }
    }

    public LanguageModelClient(String host, Model model) {
        this.httpClient = new HttpClient(host);
        this.model = model;
    }

    public LanguageModelClient(Model model) {
        this.httpClient = new HttpClient("http://161.35.192.50:11434/");
        this.model = model;
    }

    public LanguageModelClient() {
        this.httpClient = new HttpClient("http://161.35.192.50:11434/");
        this.model = Model.LLAMA3_2_3B;
    }

    public void setHost(String host) {
        httpClient.setHost(host);
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public String generateResponseNonStreaming(String prompt) throws IOException {
        // Erstellen des JSON-Payloads
        JSONObject payload = new JSONObject();
        payload.put("model", model.getModelName());
        payload.put("prompt", prompt);
        payload.put("stream", false);

        // Senden der Anfrage und Verarbeitung der Antwort
        String response = httpClient.postRequest("/api/generate", payload.toString());

        // Verarbeitung der JSON-Antwort
        JSONObject jsonResponse = new JSONObject(response);

        // Überprüfen, ob das Feld "response" existiert
        if (!jsonResponse.has("response")) {
            throw new IOException("Die API-Antwort enthält kein 'response'-Feld: " + response);
        }

        return jsonResponse.getString("response");
    }

    public String generateResponse(String prompt, boolean stream) throws IOException {
        String payload = String.format(
                "{\"model\": \"%s\", \"prompt\": \"%s\", \"stream\": %b}",
                model.getModelName(), prompt, stream
        );

        String response = httpClient.postRequest("/api/generate", payload);
        JSONObject jsonResponse = new JSONObject(response);
        return jsonResponse.getString("response");
    }


    public List<String> listModels() throws IOException {
        String response = httpClient.postRequest("/api/tags", "{}");
        JSONObject jsonResponse = new JSONObject(response);
        return jsonResponse.getJSONArray("models").toList().stream()
                .map(model -> ((JSONObject) model).getString("name"))
                .toList();
    }

    public String generateResponseWithCustomOptions(String prompt, JSONObject options, boolean stream) throws IOException {
        JSONObject payload = new JSONObject();
        payload.put("model", model.getModelName());
        payload.put("prompt", prompt);
        payload.put("options", options);
        payload.put("stream", stream);

        String response = httpClient.postRequest("/api/generate", payload.toString());
        JSONObject jsonResponse = new JSONObject(response);
        return jsonResponse.getString("response");
    }

    public void generateResponseStreaming(String prompt, Consumer<String> onPartialResponse, Consumer<Exception> onError) {
        JSONObject payload = new JSONObject();
        payload.put("model", model.getModelName());
        payload.put("prompt", prompt);

        httpClient.postRequestStreaming(
                "/api/generate",
                payload.toString(),
                partialResponse -> {
                    try {
                        JSONObject json = new JSONObject(partialResponse);
                        if (json.has("response")) {
                            onPartialResponse.accept(json.getString("response")); // Teilantwort zurückgeben
                        }
                    } catch (Exception e) {
                        onError.accept(e); // Fehler in der JSON-Verarbeitung
                    }
                },
                onError
        );
    }


}
