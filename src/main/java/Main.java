import java.io.IOException;

public class Main {
    public static void main(String[] args) {

        LanguageModelClient client = new LanguageModelClient();

        try {
            client.generateResponse("Warum ist der himmel blau?", false);
        } catch (IOException e) {
           e.printStackTrace();
        }


    }
}
