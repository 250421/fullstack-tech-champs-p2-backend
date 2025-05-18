package com.revature.nflfantasydraft.Config;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Configuration;


@Configuration
public class EnvConfig {
    public EnvConfig() {
        // Access the environment variables from the OS/Jenkins directly
        String sportsApiKey = System.getenv("SPORTSDATA_API_KEY");
        String openAI = System.getenv("OPENAI_API_KEY");

        if (sportsApiKey != null) System.setProperty("SPORTSDATA_API_KEY", sportsApiKey);
        if (openAI != null) System.setProperty("OPENAI_API_KEY", openAI);

        // For local development only. Uncomment below for running on your machine
        // Dotenv dotenv = Dotenv.configure().load();
        // dotenv.entries().forEach(entry -> {
        //     System.setProperty(entry.getKey(), entry.getValue());
        // });
    }
}
  
