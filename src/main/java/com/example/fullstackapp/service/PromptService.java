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
                String response = handleGemini(model, promptRequest.getPrompt(), promptRequest.getSystemInstruction(), promptRequest.getConversationalContext());
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
                String response = handleGroq(model, promptRequest.getPrompt(), promptRequest.getSystemInstruction(), promptRequest.getConversationalContext());
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
                String response = handleMistral(model, promptRequest.getPrompt(), promptRequest.getSystemInstruction(), promptRequest.getConversationalContext());
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
    private String handleGemini(AIModel model, String prompt, String systemInstruction, String context) throws Exception
    {
        String finalBody = "";
        if (context == null || context.isEmpty())
        {
            String body = 
        """
        {
            "contents": 
            [
                {
                    "role": "user",
                    "parts": 
                    [
                        {
                            "text": "%s"
                        },
                    ]
                },
            ],
            "systemInstruction": 
            {
                "parts": 
                [
                    {
                        "text": "%s"
                    },
                ]
            },
            "generationConfig": 
            {
                "responseMimeType": "text/plain"
            },
            "safetySettings": 
            [
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
                },
            ],
        }
        """;
        finalBody = String.format(body,prompt, systemInstruction);
        }
        else
        {
            String body = 
        """
        {
            "contents": 
            [
                {
                    "role": "user",
                    "parts": 
                    [
                        {
                            "text": "[Context]: %s. [Prompt]: %s"
                        },
                    ]
                },
            ],
            "systemInstruction": 
            {
                "parts": 
                [
                    {
                        "text": "%s"
                    },
                ]
            },
            "generationConfig": 
            {
                "responseMimeType": "text/plain"
            },
            "safetySettings": 
            [
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
                },
            ],
        }
        """;
        finalBody = String.format(body,context, prompt, systemInstruction);
        }
        String url = model.getApiUrl() + "?key=" + model.getAuthKey();
        return sendHttpReq(url, finalBody);
    }
    
    private String handleGroq(AIModel model, String prompt, String systemInstruction, String context) throws Exception
    {
        String finalBody = "";
        if (context == null || context.isEmpty())
        {
            String body = 
            """
            {
                "model": "llama-3.1-8b-instant",
                "messages": 
                [
                    {
                        "role": "user",
                        "content": "%s"
                    },
                    {
                        "role": "system",
                        "content": "%s"
                    }
                ],
                
                "temperature": 0.6,
                "max_completion_tokens": 4096,
                "top_p": 0.95,
                "stream": false,
                "stop": null
            }
            """;
        finalBody = String.format(body,prompt, systemInstruction);
        }
        else
        {
            String body = 
        """
        {
            "model": "llama-3.1-8b-instant",
            "messages": 
            [
                {
                    "role": "user",
                    "content": "[Context]: %s. [Prompt]: %s"
                },
                {
                    "role": "system",
                    "content": "%s"
                }
            ],
            
            "temperature": 0.6,
            "max_completion_tokens": 4096,
            "top_p": 0.95,
            "stream": false,
            "stop": null
        }
        """;
            finalBody = String.format(body,context, prompt, systemInstruction);
        }
        return sendGroqHttpReq(model.getApiUrl(), finalBody, model.getAuthKey());
    }
    
    private String handleMistral(AIModel model, String prompt, String systemInstruction, String context) throws Exception
    {
        String finalBody = "";
        if (context == null || context.isEmpty())
        {
            String body = 
            """
                {
                    "model": "mistral-large-latest",
                    "messages": 
                    [
                        {
                            "role": "user",
                            "content": "%s. %s"
                        }
                    ]
                }
            """;
            finalBody = String.format(body,systemInstruction,prompt);
        }
        else
        {
            String body = 
            """
                {
                    "model": "mistral-large-latest",
                    "messages": 
                    [
                        {
                            "role": "user",
                            "content": "%s. [Context]: %s. [Prompt]: %s"
                        }
                    ]
                }
            """;
            finalBody = String.format(body,systemInstruction,context, prompt);
        }
        return sendAuthHttpReq(model.getApiUrl(), finalBody, model.getAuthKey());
    }

    private String sendHttpReq(String url, String body) throws Exception{
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url))
        .header("Content-Type", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString(body))
        .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

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
