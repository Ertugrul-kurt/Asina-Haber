from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from typing import Optional
import datetime
from motor.motor_asyncio import AsyncIOMotorClient
from google import genai
import httpx
import bcrypt
from deep_translator import GoogleTranslator
import os

app = FastAPI()

GEMINI_API_KEY = os.getenv("GEMINI_API_KEY")
NEWS_API_KEY = os.getenv("NEWS_API_KEY")
MONGO_URL = os.getenv("MONGO_URL")

ai_client = genai.Client(api_key=GEMINI_API_KEY)

db_client = AsyncIOMotorClient(MONGO_URL)
db = db_client["asina_haber_db"]
news_collection = db["haberler"]
users_collection = db["kullanicilar"]


def hash_password(password: str) -> str:
    return bcrypt.hashpw(password.encode("utf-8"), bcrypt.gensalt()).decode("utf-8")


def verify_password(password: str, hashed: str) -> bool:
    return bcrypt.checkpw(password.encode("utf-8"), hashed.encode("utf-8"))


class NewsItem(BaseModel):
    baslik: str
    ozet: str
    kategori: str
    tarih: Optional[str] = None


class UserCredentials(BaseModel):
    email: str
    sifre: str
    isim: Optional[str] = ""


@app.get("/")
def index():
    return {"message": "API is running."}


@app.post("/api/haber-ekle", response_model=NewsItem)
async def add_news(news: NewsItem):
    data = news.model_dump()
    if data["tarih"] is None:
        data["tarih"] = datetime.datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    result = await news_collection.insert_one(data)
    if result.inserted_id:
        return news
    raise HTTPException(status_code=400, detail="Haber eklenemedi")


@app.get("/api/haberler")
async def get_news():
    items = []
    async for doc in news_collection.find({}):
        doc["_id"] = str(doc["_id"])
        items.append(doc)
    return {"status": "ok", "count": len(items), "data": items}


@app.get("/api/canli-haberler")
async def get_live_news():
    categories = {
        "HEPSİ": "türkiye",
        "TEKNOLOJİ": "teknoloji",
        "EKONOMİ": "ekonomi",
        "SPOR": "spor",
        "MAGAZİN": "magazin"
    }

    headers = {"User-Agent": "Mozilla/5.0"}
    results = []

    async with httpx.AsyncClient() as client:
        for category, query in categories.items():
            url = f"https://newsapi.org/v2/everything?q={query}&language=tr&sortBy=publishedAt&apiKey={NEWS_API_KEY}"
            try:
                response = await client.get(url, headers=headers)
                data = response.json()
                if data.get("status") == "ok":
                    for article in data.get("articles", []):
                        if article.get("title") and article.get("urlToImage"):
                            results.append({
                                "id": article.get("url"),
                                "title": article.get("title"),
                                "description": article.get("description") or "",
                                "imageUrl": article.get("urlToImage"),
                                "category": category,
                                "source": article.get("source", {}).get("name", "Bilinmeyen")
                            })
            except Exception as e:
                print(f"Error fetching {category}: {e}")

    return results


@app.get("/api/tarihte-bugun")
async def get_today_in_history():
    now = datetime.datetime.now()
    months = ["Ocak", "Şubat", "Mart", "Nisan", "Mayıs", "Haziran",
              "Temmuz", "Ağustos", "Eylül", "Ekim", "Kasım", "Aralık"]
    date_label = f"{now.day} {months[now.month - 1]}"

    try:
        async with httpx.AsyncClient(verify=False, follow_redirects=True) as client:
            month = str(now.month).zfill(2)
            day = str(now.day).zfill(2)
            response = await client.get(
                f"https://history.muffinlabs.com/date/{month}/{day}",
                timeout=15
            )
            data = response.json()
            events = data.get("data", {}).get("Events", [])[:3]

            content = ""
            for event in events:
                year = event.get("year", "")
                text = event.get("text", "")
                try:
                    text = GoogleTranslator(source="en", target="tr").translate(text)
                except:
                    pass
                content += f"• {year}: {text}\n\n"

            return {"tarih": date_label, "icerik": content.strip()}
    except Exception as e:
        return {"tarih": date_label, "icerik": f"Hata: {str(e)}"}


@app.post("/api/kayit")
async def register(user: UserCredentials):
    existing = await users_collection.find_one({"email": user.email})
    if existing:
        raise HTTPException(status_code=400, detail="Bu email zaten kayıtlı")
    hashed = hash_password(user.sifre)
    await users_collection.insert_one({
        "email": user.email,
        "isim": user.isim,
        "sifre": hashed,
        "admin": False
    })
    return {"mesaj": "Kayıt başarılı"}


@app.post("/api/giris")
async def login(user: UserCredentials):
    db_user = await users_collection.find_one({"email": user.email})
    if not db_user or not verify_password(user.sifre, db_user["sifre"]):
        raise HTTPException(status_code=401, detail="Email veya şifre hatalı")
    return {
        "email": db_user["email"],
        "isim": db_user["isim"],
        "admin": db_user["admin"]
    }