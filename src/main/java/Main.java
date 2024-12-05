import java.io.IOException;

public class Main {
    public static void main(String[] args) {

        LanguageModelClient client = new LanguageModelClient();

/*
        //list models
        try {
         for(String e : client.listModels()){
             System.out.println(e);
         }
        } catch (IOException e) {
           e.printStackTrace();
        }
*/
/*
        //list running models
        try {
            for(LanguageModelClient.Model m : client.listRunningModels()){
                System.out.println(m.getModelName());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
*/
        /*
        //load model
        try {
           client.loadModel(LanguageModelClient.Model.LLAMA3_2_3B);
        } catch (IOException e) {
            e.printStackTrace();
        }
*/
/*
        //chat
        try {
         String s =   client.generateResponse("warum ist der himmerl blau", false);
            System.out.println(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
*/
    }
}
