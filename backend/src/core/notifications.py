from fastapi_mail import ConnectionConfig, FastMail, MessageSchema, MessageType
from pydantic import NameEmail

from backend.src.core.config import settings
from backend.src.templates.email import EmailTemplate

conf = ConnectionConfig(
    MAIL_USERNAME = settings.smtp_username,
    MAIL_PASSWORD = settings.smtp_password,
    MAIL_SERVER = settings.smtp_server,
    MAIL_PORT = settings.smtp_port,
    MAIL_FROM = settings.smtp_from,
    MAIL_STARTTLS=True,
    MAIL_SSL_TLS=False,
    USE_CREDENTIALS=True
)

async def send_verification_email(email: str, name: str, code: str):
    template = EmailTemplate.get_verification_template()
    body = EmailTemplate.render(template, name = name, code = code)
    message = MessageSchema(
        subject="Подтверждение регистрации CacheFlow",
        recipients=[NameEmail(name, email)],
        body=body,
        subtype=MessageType.html
    )

    fm = FastMail(conf)
    await fm.send_message(message)