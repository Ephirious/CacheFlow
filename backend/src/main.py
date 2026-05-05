from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from backend.src.api import auth_router, sync_router
import uvloop
import uvicorn

uvloop.install()

app = FastAPI(title = "Cache Flow")

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(auth_router)
app.include_router(sync_router)

if __name__ == "__main__":
    uvicorn.run(app)