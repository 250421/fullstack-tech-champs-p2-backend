package com.revature.nflfantasydraft.Config;
// import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Configuration;

// uncomment next line for local developemnt
// import io.github.cdimascio.dotenv.Dotenv;


@Configuration
public class EnvConfig {
    public EnvConfig() {
        // Access the environment variables from the OS/Jenkins directly
        String apiKey = System.getenv("YOUR_API_KEY");
        String otherKey = System.getenv("YOUR_OTHER_KEY");

        if (apiKey != null) System.setProperty("YOUR_API_KEY", apiKey);
        if (otherKey != null) System.setProperty("YOUR_OTHER_KEY", otherKey);

        // For local development only. Uncomment below for running on your machine
        // Dotenv dotenv = Dotenv.configure().load();
        // dotenv.entries().forEach(entry -> {
        //     System.setProperty(entry.getKey(), entry.getValue());
        // });
    }
}
