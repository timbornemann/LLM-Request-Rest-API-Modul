import org.json.JSONObject;
import java.io.IOException;

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

    public String generateResponse(String prompt, boolean stream) throws IOException {
        String payload = String.format(
                "{\"model\": \"%s\", \"prompt\": \"%s\", \"stream\": %b}",
                model.getModelName(), prompt, stream
        );

        String response = httpClient.postRequest("/api/generate", payload);
        JSONObject jsonResponse = new JSONObject(response);
        return jsonResponse.getString("response");
    }
}
