from backend.src.models.users import EmailCode, EmailCodeAction, User
from backend.src.repositories.uow import UnitOfWork
from backend.src.schemas.email_code import EmailCodeCreateInner
from backend.src.schemas.result import Result, ErrorCode
from backend.src.schemas.user import UserCreate, UserCreateInner, UserUpdateInner
from backend.src.core import security


class AuthService:
    __slots__ = ("uow",)
    def __init__(self, uow: UnitOfWork):
        self.uow = uow

    async def register(self, body: UserCreate) -> Result[tuple[EmailCode, str]]:
        async with self.uow as uow:
            user = await uow.user_repository.get_by_email(email=body.email)
            if user:
                if user.verified:
                    return Result.err(
                        message = "Email already registered",
                        error_code = ErrorCode.EMAIL_EXISTS
                    )

                code_exists = await uow.email_code_repository.get_by_user_id(
                    user.id,
                    action = EmailCodeAction.register
                )
                if code_exists and not code_exists.is_expired:
                    return Result.ok((code_exists, "ALREADY_SENT"))

                await uow.email_code_repository.delete_by_user_id(user.id, action = EmailCodeAction.register)

            else:
                hashed_password = security.Password.encrypt(body.password)
                data = UserCreateInner(
                    email=body.email,
                    name=body.name,
                    password_hash=hashed_password,
                )

                user = await uow.user_repository.insert(data)

            code_plain = security.Password.generate_otp()

            code_hash = security.Password.encrypt(code_plain, ctx = "sha256")
            code = EmailCodeCreateInner(
                code_hash = code_hash,
                user_id = user.id,
                action = EmailCodeAction.register
            )
            code_obj = await uow.email_code_repository.insert(code)
            return Result.ok((code_obj, code_plain))

    async def verify_register(self, user: User, plain_code: str) -> Result[User]:
        async with self.uow as uow:
            code = await uow.email_code_repository.get_by_user_id(user.id, action = EmailCodeAction.register)
            if not code or code.is_expired:
                return Result.err(message = "No codes found", error_code = ErrorCode.NO_ALIVE_CODES)

            if not security.Password.verify(plain_code, code.code_hash, ctx = "sha256"):
                return Result.err(message = "Invalid code", error_code = ErrorCode.INVALID_CODE)

            update_schema = UserUpdateInner(verified=True)
            user_in_session = await uow.user_repository.get_by_id(user.id)
            new_user = await uow.user_repository.update(user_in_session, update_schema)
            await uow.email_code_repository.delete_by_user_id(
                user.id,
                action=EmailCodeAction.register
            )
            return Result.ok(new_user)