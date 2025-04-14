package com.example.fullstackapp.controller;

import com.example.fullstackapp.model.AIModel;
import com.example.fullstackapp.service.ModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

//This controller file deals with the requests. It's called by the REST APIs

@RestController
@RequestMapping("/api/models")
@CrossOrigin(origins = "http://localhost:5173")
public class ModelController {
    @Autowired
    private ModelService modelService;

    @GetMapping
    public List<AIModel> getAllAIModels()
    {
        return modelService.getAllAiModels();
    }

    @GetMapping("/{id}")
    public Optional<AIModel> getModelById(@PathVariable Long id)
    {
        return modelService.getModelById(id);
    }

    @PostMapping
    public AIModel createModel(@RequestBody AIModel model)
    {
        return modelService.createModel(model);
    }

    @PutMapping("/{id}")
    public AIModel updateModel(@PathVariable Long id, @RequestBody AIModel modeldetails)
    {
        return modelService.updateModel(id, modeldetails);
    }

    @DeleteMapping("{id}")
    public void deleteModel(@PathVariable Long id)
    {
        modelService.deleteModel(id);
    }

}
