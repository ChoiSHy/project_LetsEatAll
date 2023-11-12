import os
import openai
from dotenv import load_dotenv

from youtube_transcript_api import YouTubeTranscriptApi

srt = YouTubeTranscriptApi.get_transcript('TxFnjrwDcZs', languages=['ko'])
texts = []
for i in srt:
    print("{}\n".format(i))
    texts.append(i['text'])

print(texts)


'''load_dotenv()
openai.api_key = os.getenv("OPENAI_API_KEY")

message = []
message.append({"role": "user", "content": str(texts)+'를 요약해줘'})
completion = openai.ChatCompletion.create(
    model="gpt-3.5-turbo",
    messages=message
)
chat_response = completion.choices[0].message.content
print(f'ChatGPT: {chat_response}')
message.append({"role": "assistant", "content": chat_response})'''
