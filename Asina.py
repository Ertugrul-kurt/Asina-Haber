from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from typing import Optional
import datetime
from motor.motor_asyncio import AsyncIOMotorClient
from google import genai
import httpx
import bcrypt

# --- GEMINI YAPAY ZEKA AYARLARI ---
ai_client = genai.Client(api_key="AIzaSyB4X4FP1KMTVBwKvCYTXu7kKYZoP6Z_1LA")

# --- NEWS API AYARI ---
NEWS_API_KEY = "88fc0d6b9ac04ec986be189a6106d0ff"

app = FastAPI(title="Aşina Haber API")

# --- MONGODB BAĞLANTI AYARLARI ---
MONGO_URL = "mongodb+srv://asina_admin:admin53@kurt.kub1jnj.mongodb.net/?retryWrites=true&w=majority"
db_client = AsyncIOMotorClient(MONGO_URL)
database = db_client["asina_haber_db"]
haberler_koleksiyonu = database["haberler"]
kullanicilar = database["kullanicilar"]

# --- ŞİFRE HASHLEME ---
def sifreyi_hashle(sifre: str) -> str:
    return bcrypt.hashpw(sifre.encode("utf-8"), bcrypt.gensalt()).decode("utf-8")

def sifreyi_dogrula(sifre: str, hash: str) -> bool:
    return bcrypt.checkpw(sifre.encode("utf-8"), hash.encode("utf-8"))


# --- MODELLER ---
class Haber(BaseModel):
    baslik: str
    ozet: str
    kategori: str
    tarih: Optional[str] = None


class KullaniciKayit(BaseModel):
    email: str
    sifre: str
    isim: Optional[str] = ""


# --- ANA SAYFA ---
@app.get("/")
def ana_sayfa():
    return {"mesaj": "Aşina Haber API'sine Hoş Geldiniz!"}


# --- HABER EKLE ---
@app.post("/api/haber-ekle", response_model=Haber)
async def haber_ekle(haber: Haber):
    haber_dict = haber.model_dump()
    if haber_dict['tarih'] is None:
        haber_dict['tarih'] = datetime.datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    yeni_haber = await haberler_koleksiyonu.insert_one(haber_dict)
    if yeni_haber.inserted_id:
        return haber
    raise HTTPException(status_code=400, detail="Haber eklenemedi")


# --- MongoDB HABERLERİ GETİR ---
@app.get("/api/haberler")
async def haberleri_getir():
    haberler_listesi = []
    cursor = haberler_koleksiyonu.find({})
    async for belge in cursor:
        belge["_id"] = str(belge["_id"])
        haberler_listesi.append(belge)
    return {"durum": "basarili", "haber_sayisi": len(haberler_listesi), "haberler": haberler_listesi}


# --- CANLI HABER ÇEK ---
@app.get("/api/canli-haberler")
async def canli_haberleri_getir():
    kategori_map = {
        "HEPSİ": "türkiye",
        "TEKNOLOJİ": "teknoloji",
        "EKONOMİ": "ekonomi",
        "SPOR": "spor",
        "MAGAZİN": "magazin"
    }

    headers = {"User-Agent": "Mozilla/5.0"}
    tum_haberler = []

    async with httpx.AsyncClient() as client:
        for kategori, arama in kategori_map.items():
            url = f"https://newsapi.org/v2/everything?q={arama}&language=tr&sortBy=publishedAt&apiKey={NEWS_API_KEY}"
            try:
                response = await client.get(url, headers=headers)
                data = response.json()

                if data.get("status") == "ok":
                    for article in data.get("articles", []):
                        if article.get("title") and article.get("urlToImage"):
                            haber = {
                                "id": article.get("url"),
                                "title": article.get("title"),
                                "description": article.get("description") or "",
                                "imageUrl": article.get("urlToImage"),
                                "category": kategori,
                                "source": article.get("source", {}).get("name", "Bilinmeyen")
                            }
                            tum_haberler.append(haber)
            except Exception as e:
                print(f"{kategori} haberleri alınamadı: {e}")

    return tum_haberler


# --- TARİHTE BUGÜN ---
@app.get("/api/tarihte-bugun")
async def tarihte_bugun_getir():
    bugun = datetime.datetime.now()
    aylar = ["Ocak", "Şubat", "Mart", "Nisan", "Mayıs", "Haziran",
             "Temmuz", "Ağustos", "Eylül", "Ekim", "Kasım", "Aralık"]
    tarih_metni = f"{bugun.day} {aylar[bugun.month - 1]}"

    prompt = f"""
    Bugün günlerden {tarih_metni}. Bana tarihte bugün yaşanmış en önemli 3 olayı kısa maddeler halinde yaz. 
    Mümkünse maddelerden biri İstanbul'un tarihi, saray mimarisi veya eski şehir efsaneleriyle ilgili ilginç bir detay olsun. 
    Sadece maddeleri yaz, giriş veya çıkış cümlesi kurma. Mobil uygulamada okunacağı için çok uzun olmasın.
    """

    try:
        cevap = await ai_client.aio.models.generate_content(
            model='gemini-2.0-flash',
            contents=prompt
        )
        return {"tarih": tarih_metni, "icerik": cevap.text}
    except Exception as e:
        return {"tarih": tarih_metni, "icerik": f"İçerik oluşturulamadı: {str(e)}"}


# --- KULLANICI KAYIT ---
@app.post("/api/kayit")
async def kayit_ol(kullanici: KullaniciKayit):
    mevcut = await kullanicilar.find_one({"email": kullanici.email})
    if mevcut:
        raise HTTPException(status_code=400, detail="Bu email zaten kayıtlı")
    hashli_sifre = sifreyi_hashle(kullanici.sifre)
    await kullanicilar.insert_one({
        "email": kullanici.email,
        "isim": kullanici.isim,
        "sifre": hashli_sifre,
        "admin": False
    })
    return {"mesaj": "Kayıt başarılı"}


# --- KULLANICI GİRİŞ ---
@app.post("/api/giris")
async def giris_yap(kullanici: KullaniciKayit):
    db_kullanici = await kullanicilar.find_one({"email": kullanici.email})
    if not db_kullanici or not sifreyi_dogrula(kullanici.sifre, db_kullanici["sifre"]):
        raise HTTPException(status_code=401, detail="Email veya şifre hatalı")
    return {
        "email": db_kullanici["email"],
        "isim": db_kullanici["isim"],
        "admin": db_kullanici["admin"]
    }