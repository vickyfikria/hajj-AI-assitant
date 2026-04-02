
import os
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from openai import OpenAI

# Initialize FastAPI app
app = FastAPI()

# Initialize OpenAI client (API key will be read from environment variable OPENAI_API_KEY)
client = OpenAI()

# In-memory storage for Hajj/Umrah context (can be replaced with a database later)
hajj_context = """
This AI assistant provides guidance and prayer suggestions for Hajj and Umrah pilgrims. 
It should offer respectful, spiritual, and informative advice based on Islamic teachings.

Key Hajj Rituals:
1. Ihram: State of consecration, wearing special garments. Dua: Talbiyah ('Labbayka Allahumma Labbayk...').
2. Tawaf: Circumambulating the Kaaba seven times. Dua: 'Rabbana atina fid-dunya hasanatan wa fil-akhirati hasanatan wa qina 'adhaban-nar' (between Yemeni Corner and Black Stone).
3. Sa'i: Walking seven times between Safa and Marwa. Dua: Recite Quranic verses (e.g., Surah Al-Baqarah 2:158) and personal supplications.
4. Wuquf (Arafat): Standing on Mount Arafat on 9th Dhul-Hijjah. This is the peak of Hajj. Dua: Best time for supplication.
5. Muzdalifah: Collecting pebbles and spending the night after Arafat.
6. Rami (Stoning): Throwing pebbles at the Jamarat (pillars).
7. Halq/Taqsir: Shaving (men) or trimming (women) hair.
8. Qurbani: Animal sacrifice.
9. Tawaf al-Ifadah: The main Tawaf of Hajj.
10. Tawaf al-Wada: Farewell Tawaf before leaving Makkah.

Key Umrah Rituals:
1. Ihram
2. Tawaf
3. Sa'i
4. Halq/Taqsir

General Duas:
- For travel: 'Subhanalladhi sakhkhara lana hadha wama kunna lahu muqrinin wa inna ila Rabbina lamunqalibun.'
- Upon arrival: 'Allahummaftah li abwaba rahmatik.'
- General well-being: 'Allahumma inni as'aluka al-afiyah fid-dunya wal-akhirah.'

Important Note: For official religious rulings (fatwas), always consult qualified Islamic scholars.
"""

class ChatRequest(BaseModel):
    message: str

class ChatResponse(BaseModel):
    response: str

@app.post("/chat", response_model=ChatResponse)
async def chat_with_ai(request: ChatRequest):
    try:
        # Combine Hajj context with user's message
        full_prompt = f"""{hajj_context}

User: {request.message}
AI Assistant:"""

        # Call OpenAI API
        completion = client.chat.completions.create(
            model="gpt-4.1-mini",  # Using a suitable model for chat
            messages=[
                {"role": "system", "content": "You are a helpful AI assistant providing guidance for Hajj and Umrah pilgrims. Always refer to the provided Hajj context."},
                {"role": "user", "content": full_prompt}
            ],
            max_tokens=500,
            temperature=0.7,
        )
        
        ai_response = completion.choices[0].message.content
        return ChatResponse(response=ai_response)
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.get("/health")
async def health_check():
    return {"status": "ok"}

