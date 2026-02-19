from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from typing import List, Dict
import spacy
from sentence_transformers import SentenceTransformer, util
from textstat import flesch_reading_ease

# Load NLP models
nlp = spacy.load("en_core_web_sm")
model = SentenceTransformer('sentence-transformers/all-MiniLM-L6-v2')

# FastAPI app
app = FastAPI()

# Sample reference prompt
reference_prompt = "Generate a Python script that scrapes top 5 headlines from Hacker News using requests and BeautifulSoup."

# Pydantic models
class RubricCriterion(BaseModel):
    id: str
    name: str
    description: str
    weight: float
    maxScore: int

class Rubric(BaseModel):
    rubricName: str
    criteria: List[RubricCriterion]

class PromptInput(BaseModel):
    prompt: str
    rubric: Rubric

@app.post("/grade")
def grade_prompt(data: PromptInput):
    prompt = data.prompt
    rubric = data.rubric

    doc = nlp(prompt)
    prompt_embedding = model.encode(prompt, convert_to_tensor=True)
    ref_embedding = model.encode(reference_prompt, convert_to_tensor=True)

    results = {}

    # Clarity
    clarity_score = min(max((flesch_reading_ease(prompt) - 30) / 70, 0), 1) * 5
    results["clarity"] = round(clarity_score, 2)

    # Specificity
    named_entities = len(doc.ents)
    specificity_score = min(named_entities / 4.0, 1.0) * 5
    results["specificity"] = round(specificity_score, 2)

    # Completeness
    verb_chunks = len([tok for tok in doc if tok.pos_ == "VERB"])
    noun_chunks = len(list(doc.noun_chunks))
    completeness_score = min((verb_chunks + noun_chunks) / 8.0, 1.0) * 5
    results["completeness"] = round(completeness_score, 2)

    # Conciseness
    token_count = len(doc)
    conciseness_score = max(1 - (token_count - 50) / 50, 0) * 5 if token_count > 50 else 5
    results["conciseness"] = round(conciseness_score, 2)

    # Relevance
    sim_score = float(util.cos_sim(prompt_embedding, ref_embedding)[0][0])
    relevance_score = min(sim_score / 0.9, 1.0) * 5
    results["relevance"] = round(relevance_score, 2)

    # Final score
    final_score = 0
    for criterion in rubric.criteria:
        cid = criterion.id
        if cid in results:
            final_score += results[cid] * criterion.weight

    results["final_score"] = round(final_score, 2)
    return results
