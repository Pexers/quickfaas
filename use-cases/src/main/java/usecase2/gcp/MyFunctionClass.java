package usecase2.gcp;

import com.google.cloud.translate.TranslateOptions;
import quickfaas.triggers.http.HttpRequestQf;
import quickfaas.triggers.http.HttpResponseQf;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class MyFunctionClass {  // translate-text

    public void myFunction(HttpRequestQf req, HttpResponseQf res) {
        String toTranslate = req.getQueryParameter("translate");
        String translation = translateAndSend(toTranslate);
        res.send(200, "Your translation: " + translation);
    }

    private String translateAndSend(String toTranslate) {
        String translation = TranslateOptions.getDefaultInstance().getService().translate(toTranslate).getTranslatedText();
        HttpRequest request = HttpRequest.newBuilder(URI.create("https://quickfaas-java-app.azurewebsites.net/api/store-translation"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{\"translation\": \"" + translation + "\"}")).build();
        HttpClient.newHttpClient().sendAsync(request, HttpResponse.BodyHandlers.discarding());
        return translation;
    }

}
