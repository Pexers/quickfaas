package usecase2.msazure;

import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosClientBuilder;
import quickfaas.configurations.ConfigurationsQf;
import quickfaas.triggers.http.HttpRequestQf;
import quickfaas.triggers.http.HttpResponseQf;

public class MyFunctionClass {  // store-translation

    public void myFunction(HttpRequestQf req, HttpResponseQf res) {
        String translation = req.getFromJsonBody("translation").getAsString();
        storeTranslation(translation);
        res.send(200, "Translation '" + translation + "' was stored.");
    }

    private void storeTranslation(String translation) {
        Entry entry = new Entry();
        entry.translation = translation;
        String cosmosKey = ConfigurationsQf.getConfiguration("cosmosKey").getAsString();
        CosmosClient cosmosClient = new CosmosClientBuilder().endpoint("https://quickfaas-cosmos.documents.azure.com:443/").key(cosmosKey).buildClient();
        cosmosClient.getDatabase("QuickFaaSDB").getContainer("Translations").createItem(entry);
        cosmosClient.close();
    }

    public static class Entry {
        public String id = java.util.UUID.randomUUID().toString();
        public String translation;
    }

}

