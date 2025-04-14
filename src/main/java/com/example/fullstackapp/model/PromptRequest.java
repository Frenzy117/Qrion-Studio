package com.example.fullstackapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PromptRequest {
    private String modelName;
    private String systemInstruction;
    private String conversationalContext;
    private String prompt;
}
