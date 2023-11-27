import torch
from transformers import AutoTokenizer, AutoModelForSequenceClassification
from fastapi import FastAPI
from pydantic import BaseModel
from starlette.responses import JSONResponse

# GPU가 사용 가능한 경우 'cuda:0'로 설정, 그렇지 않은 경우 CPU로 설정
device = torch.device('cuda:0' if torch.cuda.is_available() else 'cpu')
MODEL_NAME = "beomi/KcELECTRA-base-v2022"

# Tokenizer 초기화
tokenizer = AutoTokenizer.from_pretrained(MODEL_NAME)

# 모델 불러오기
model = AutoModelForSequenceClassification.from_pretrained(MODEL_NAME)
model.load_state_dict(torch.load('C:/Users/HOME/Desktop/2023/working/Seeker/HaterSeeker_model.pth', map_location=device))
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
app = FastAPI()

class Item(BaseModel):
    content: str

@app.post("/")
async def predict(item : Item):
    dicted_item = dict(item)
    dicted_item['success'] = sentence_predict(dicted_item['content'])    
    return JSONResponse(dicted_item)


