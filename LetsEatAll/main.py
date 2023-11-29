import torch
import os
import uvicorn
import signal

from transformers import AutoTokenizer, AutoModelForSequenceClassification
from fastapi import FastAPI, Response
from pydantic import BaseModel
from starlette.responses import JSONResponse

# GPU가 사용 가능한 경우 'cuda:0'로 설정, 그렇지 않은 경우 CPU로 설정
device = torch.device('cuda:0' if torch.cuda.is_available() else 'cpu')
MODEL_NAME = "beomi/KcELECTRA-base-v2022"

# Tokenizer 초기화
tokenizer = AutoTokenizer.from_pretrained(MODEL_NAME)

# 모델 불러오기
model = AutoModelForSequenceClassification.from_pretrained(MODEL_NAME)
model.load_state_dict(torch.load('HaterSeeker_model.pth', map_location=device))
model.to(device)
model.eval()

def sentence_predict(sent):
    tokenized_sent = tokenizer(
        sent,
        return_tensors='pt',
        truncation=True,
        add_special_tokens=True,
        max_length=128
    )
    tokenized_sent.to(device)

    with torch.no_grad():
        outputs = model(
            input_ids=tokenized_sent['input_ids'],
            attention_mask=tokenized_sent['attention_mask'],
            token_type_ids=tokenized_sent['token_type_ids']
        )
    
    logits = outputs.logits
    logits = logits.detach().cpu()
    result = logits.argmax(-1)
    if result == 0:
        result = False
    elif result == 1:
        result = True
    return result

# --- Fast API setting ---

class Item(BaseModel):
    content: str

# --- main ---
if __name__  ==  "__main__":
    print("Fast API is set")
    app = FastAPI()
    @app.post("/")
    async def predict(item : Item):
        dicted_item = dict(item)
        dicted_item['success'] = sentence_predict(dicted_item['content'])    
        return JSONResponse(dicted_item)
    def shutdown():
        os.kill(os.getpid(), signal.SIGTERM)
        return Response(status_code=200, content="Server shutting down...")

    @app.on_event("shutdown")
    def exit():
        print('Server shutting down...')

    app.add_api_route('/shutdown', shutdown, methods=['GET'])
    uvicorn.run(app, host='0.0.0.0', port=8000)
