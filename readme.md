# Hajj AI Assistant

This project consists of a FastAPI Python backend and a native Kotlin Android client application. The AI assistant provides guidance and prayer suggestions for Hajj and Umrah pilgrims.

## Project Structure

*hajj_ai_backend/: Contains the FastAPI backend application.

*hajj_ai_android/: Contains the native Kotlin Android client application.

Backend Setup (FastAPI)

1. Navigate to the backend directory:
```
cd hajj_ai_backend
```

2. Create and activate a virtual environment:
```
python3.11 -m venv venv
source venv/bin/activate
```

3. Install dependencies:
```
pip install fastapi uvicorn openai
```

4. Set your OpenAI API Key:
The application expects your OpenAI API key to be set as an environment variable named `OPENAI_API_KEY`.
```
export OPENAI_API_KEY="YOUR_OPENAI_API_KEY"
```

Replace `YOUR_OPENAI_API_KEY` with your actual OpenAI API key.

5. Run the FastAPI application:

```
uvicorn main:app --host 0.0.0.0 --port 8000
```

