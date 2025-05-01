package com.example.fullstackapp.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.fullstackapp.model.AIModel;
import com.example.fullstackapp.model.PromptRequest;
import com.example.fullstackapp.repository.ModelRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class PromptService {

    @Autowired
    private ModelRepository modelRepository;
    
    public String handlePrompt(PromptRequest promptRequest) throws Exception {
        AIModel model = modelRepository.findByName(promptRequest.getModelName()).orElseThrow(() -> new RuntimeException("Model not found"));
        String provider = model.getProvider();
        System.out.println("Model from Request: " + promptRequest.getModelName());
        System.out.println("System Instruction from Request: " + promptRequest.getSystemInstruction());
        System.out.println("Context from Request: " + promptRequest.getConversationalContext());
        System.out.println("Prompt from Request: " + promptRequest.getPrompt());
        ObjectMapper mapper = new ObjectMapper();
        

        switch (provider.toLowerCase())
        {
            case "google":
            {
                String response = handleGemini(model, 
                promptRequest.getPrompt(), 
                promptRequest.getSystemInstruction(), 
                promptRequest.getConversationalContext(), 
                promptRequest.getTemperature(), 
                promptRequest.getTopP(),
                promptRequest.getMaxTokens());
                JsonNode root = mapper.readTree(response);
                String responseText = root.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();
                return responseText;
            }
            case "groq":
            {
                String response = handleGroq(
                    model, 
                    promptRequest.getPrompt(), 
                    promptRequest.getSystemInstruction(), 
                    promptRequest.getConversationalContext(),
                    promptRequest.getTopP(),
                    promptRequest.getTemperature(),
                    promptRequest.getMaxTokens()
                );
                JsonNode root = mapper.readTree(response);
                String responseText = root.path("choices")
                .get(0)
                .path("message")
                .path("content")
                .asText();
                return responseText;
            }
            case "mistral":
            {
                String response = handleMistral(model, 
                promptRequest.getPrompt(), 
                promptRequest.getSystemInstruction(), 
                promptRequest.getConversationalContext(), 
                promptRequest.getTopP(), 
                promptRequest.getTemperature(),
                promptRequest.getMaxTokens());
                JsonNode root = mapper.readTree(response);
                String responseText = root.path("choices")
                .get(0)
                .path("message")
                .path("content")
                .asText();
                return responseText;
            }
            default:
                throw new RuntimeException("Model provider not supported.");
        }
    }

    /**
     * Handles the Gemini API request.
     * @param model The AI model to use.
     * @param prompt The prompt to send to the model.
     * @param systemInstruction The system instruction to send to the model.
     * @param context The conversational context to send to the model.
     * @param topP The top P value for sampling.
     * @param temperature The temperature for sampling.
     * @param maxTokens The maximum number of tokens to generate.
     * @return The response from the Gemini API.
     */
    private String handleGemini(AIModel model, 
    String prompt, 
    String systemInstruction, 
    String context, 
    float temperature, 
    float topP,
    int maxTokens) throws Exception
   {
      String contents;
    
      if (context == null || context.isEmpty()) {
         contents = String.format("""
               [
                  {
                     "role": "user",
                     "parts": [
                           {
                              "text": "%s"
                           }
                     ]
                  }
               ]
               """, prompt);
      } else {
         contents = String.format("""
               [
                  {
                     "role": "user",
                     "parts": [
                           {
                              "text": "[Context]: %s. [Prompt]: %s"
                           }
                     ]
                  }
               ]
               """, context, prompt);
      }

      String systemInstrBlock = "";
      if (systemInstruction != null && !systemInstruction.isEmpty()) {
         systemInstrBlock = String.format("""
               ,"systemInstruction": {
                  "parts": [
                     {
                           "text": "%s"
                     }
                  ]
               }
               """, systemInstruction);
      }

      String finalBody = String.format("""
         {
               "contents": %s%s,
               "generationConfig": {
                  "temperature": %f,
                  "topP": %f,
                  "maxOutputTokens": %d,
                  "responseMimeType": "text/plain"
               },
               "safetySettings": [
                  {
                     "category": "HARM_CATEGORY_HARASSMENT",
                     "threshold": "BLOCK_LOW_AND_ABOVE"
                  },
                  {
                     "category": "HARM_CATEGORY_HATE_SPEECH",
                     "threshold": "BLOCK_LOW_AND_ABOVE"
                  },
                  {
                     "category": "HARM_CATEGORY_SEXUALLY_EXPLICIT",
                     "threshold": "BLOCK_LOW_AND_ABOVE"
                  },
                  {
                     "category": "HARM_CATEGORY_DANGEROUS_CONTENT",
                     "threshold": "BLOCK_LOW_AND_ABOVE"
                  }
               ]
         }
         """, contents, systemInstrBlock, temperature, topP, maxTokens);

      String url = model.getApiUrl() + "?key=" + model.getAuthKey();
      return sendHttpReq(url, finalBody);
}


   /**
    * Handles the Groq API request.
    * @param model The AI model to use.
    * @param prompt The prompt to send to the model.
    * @param systemInstruction The system instruction to send to the model.
    * @param context The conversational context to send to the model.
    * @param topP The top P value for sampling.
    * @param temperature The temperature for sampling.
    * @param maxTokens The maximum number of tokens to generate.
    * @return The response from the Groq API.
    */
   private String handleGroq(AIModel model, 
    String prompt, 
    String systemInstruction, 
    String context,
    float topP, 
    float temperature,
    int maxTokens) throws Exception
   {
      String messages;
    
      if (systemInstruction == null || systemInstruction.isEmpty()) {
         if (context == null || context.isEmpty()) {
               messages = String.format("""
                  [
                     {
                           "role": "user",
                           "content": "%s"
                     }
                  ]
                  """, prompt);
         } else {
               messages = String.format("""
                  [
                     {
                           "role": "user",
                           "content": "[Context]: %s. [Prompt]: %s"
                     }
                  ]
                  """, context, prompt);
         }
      } else {
         if (context == null || context.isEmpty()) {
               messages = String.format("""
                  [
                     {
                           "role": "system",
                           "content": "%s"
                     },
                     {
                           "role": "user",
                           "content": "%s"
                     }
                  ]
                  """, systemInstruction, prompt);
         } else {
               messages = String.format("""
                  [
                     {
                           "role": "system",
                           "content": "%s"
                     },
                     {
                           "role": "user",
                           "content": "[Context]: %s. [Prompt]: %s"
                     }
                  ]
                  """, systemInstruction, context, prompt);
         }
      }

      String finalBody = String.format("""
         {
               "model": "llama-3.1-8b-instant",
               "messages": %s,
               "temperature": %f,
               "top_p": %f,
               "max_completion_tokens": %d,
               "stream": false,
               "stop": null
         }
         """, messages, temperature, topP, maxTokens);

      return sendGroqHttpReq(model.getApiUrl(), finalBody, model.getAuthKey());
}

   /**
   * Handles the Mistral API request.
   * @param model The AI model to use.
   * @param prompt The prompt to send to the model.
   * @param systemInstruction The system instruction to send to the model.
   * @param context The conversational context to send to the model.
   * @param topP The top P value for sampling.
   * @param temperature The temperature for sampling.
   * @param maxTokens The maximum number of tokens to generate.
   * @return The response from the Mistral API.
   */
   private String handleMistral(AIModel model, 
   String prompt, 
   String systemInstruction, 
   String context, 
   float topP, 
   float temperature, 
   int maxTokens) throws Exception
   {
      String userContent;

      if ((systemInstruction == null || systemInstruction.isEmpty()) &&
            (context == null || context.isEmpty())) {
            userContent = prompt;
      } else if (systemInstruction != null && !systemInstruction.isEmpty() &&
               (context == null || context.isEmpty())) {
            userContent = systemInstruction + ". " + prompt;
      } else if ((systemInstruction == null || systemInstruction.isEmpty()) &&
               context != null && !context.isEmpty()) {
            userContent = "[Context]: " + context + ". [Prompt]: " + prompt;
      } else {
            userContent = systemInstruction + ". [Context]: " + context + ". [Prompt]: " + prompt;
      }

      String finalBody = String.format("""
            {
               "model": "mistral-large-latest",
               "messages": [
                  {
                        "role": "user",
                        "content": "%s"
                  }
               ],
               "temperature": %f,
               "top_p": %f,
               "max_tokens": %d
            }
            """, userContent, temperature, topP, maxTokens);

      return sendAuthHttpReq(model.getApiUrl(), finalBody, model.getAuthKey());
   };

   /**
   * Sends an HTTP request to the specified URL with the given body.
   * @param url The URL to send the request to.
   * @param body The body of the request.
   * @return The response from the server.
   * @throws Exception If an error occurs while sending the request.
   */
    private String sendHttpReq(String url, String body) throws Exception{
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url))
        .header("Content-Type", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString(body))
        .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

      /**
      * Sends an authenticated HTTP request to the specified URL with the given body.
      * @param url The URL to send the request to.
      * @param body The body of the request.
      * @param apiKey The API key for authentication.
      * @return The response from the server.
      * @throws Exception If an error occurs while sending the request.
      */
    private String sendAuthHttpReq(String url, String body, String apiKey) throws Exception{
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url))
        .header("Content-Type", "application/json")
        .header("Authorization","Bearer " + apiKey)
        .header("Accept","application/json")
        .POST(HttpRequest.BodyPublishers.ofString(body))
        .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

      /**
      * Sends an authenticated HTTP request to the specified URL with the given body.
      * @param url The URL to send the request to.
      * @param body The body of the request.
      * @param apiKey The API key for authentication.
      * @return The response from the server.
      * @throws Exception If an error occurs while sending the request.
      */
    private String sendGroqHttpReq(String url, String body, String apiKey) throws Exception{
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url))
        .header("Content-Type", "application/json")
        .header("Authorization","Bearer " + apiKey)
        .POST(HttpRequest.BodyPublishers.ofString(body))
        .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }
}
