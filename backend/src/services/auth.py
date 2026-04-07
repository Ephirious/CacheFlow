from uuid import UUID
from backend.src.models.users import EmailCode, EmailCodeAction, User
from backend.src.repositories.base import AlreadyExists
from backend.src.repositories.uow import UnitOfWork
from backend.src.schemas.auth import TokenModel, UserLogin
from backend.src.schemas.email_code import EmailCodeCreateInner
from backend.src.schemas.result import Result, ErrorCode
from backend.src.schemas.user import UserCreate, UserCreateInner, UserUpdateInner
from backend.src.core import security


class AuthService:
    __slots__ = ("uow",)
    def __init__(self, uow: UnitOfWork):
        self.uow = uow

    async def register(self, body: UserCreate) -> Result[tuple[User, str]]:
        async with self.uow as uow:
            hashed_password = security.Password.encrypt(body.password)
            data = UserCreateInner(
                email=body.email,
                name=body.name,
                password_hash=hashed_password,
            )
            try:
                user = await uow.user_repository.insert(data)
            except AlreadyExists:
               
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
                        return Result.ok((user, "ALREADY_SENT"))

                    await uow.email_code_repository.delete_by_user_id(user.id, action = EmailCodeAction.register)

            code_plain = security.Password.generate_otp()

            code_hash = security.Password.encrypt(code_plain, ctx = "sha256")
            code = EmailCodeCreateInner(
                code_hash = code_hash,
                user_id = user.id,
                action = EmailCodeAction.register
            )
            await uow.email_code_repository.insert(code)
            return Result.ok((user, code_plain))

    async def verify_register(self, user_id: UUID, plain_code: str) -> Result[User]:
        async with self.uow as uow:
            user = await uow.user_repository.get_by_id(user_id)

            if not user:
                return Result.err(message = "User not found", error_code = ErrorCode.INVALID_CREDENTIALS)

            if user.verified:
                return Result.ok(user)
            
            code = await uow.email_code_repository.get_by_user_id(user.id, action = EmailCodeAction.register)
            if not code or code.is_expired:
                return Result.err(message = "No codes found", error_code = ErrorCode.NO_ALIVE_CODES)

            if not security.Password.verify(plain_code, code.code_hash, ctx = "sha256"):
                return Result.err(message = "Invalid code", error_code = ErrorCode.INVALID_CODE)

            update_schema = UserUpdateInner(verified=True)
            new_user = await uow.user_repository.update(user, update_schema)
            await uow.email_code_repository.delete_by_user_id(
                user.id,
                action=EmailCodeAction.register
            )
            return Result.ok(new_user)
        
    async def resend_verification_code(self, user_id: UUID) -> Result[tuple[User, str]]:
        async with self.uow as uow:
            user = await uow.user_repository.get_by_id(user_id)
            if not user or user.verified:
                return Result.err("Request ignored", ErrorCode.ALREADY_VERIFIED)
            
            existing_code = await uow.email_code_repository.get_by_user_id(
                user.id, action=EmailCodeAction.register
            )
            if existing_code and not existing_code.is_expired:
                return Result.ok((user, "ALREADY_SENT"))
            
            await uow.email_code_repository.delete_by_user_id(user.id, action=EmailCodeAction.register)
            
            code_plain = security.Password.generate_otp()
            code_hash = security.Password.encrypt(code_plain, ctx="sha256")
            
            new_code_data = EmailCodeCreateInner(
                code_hash=code_hash,
                user_id=user.id,
                action=EmailCodeAction.register
            )
            await uow.email_code_repository.insert(new_code_data)
            
            return Result.ok((user, code_plain))


        
    async def login(self, body: UserLogin) -> Result[TokenModel]:
        async with self.uow as uow:
            user = await uow.user_repository.get_by_email(body.email)
            if not user:
                return Result.err(message = "Invalid login or password", error_code=ErrorCode.INVALID_CREDENTIALS)
            
            if not user.verified:
                return Result.err(message = "User not verificated", error_code = ErrorCode.FORBIDDEN)

            if not security.Password.verify(body.password, user.password_hash):
                return Result.err(message = "Invalid login or password", error_code = ErrorCode.INVALID_CREDENTIALS)
            
            tokens = security.Token.create_token(user_id = user.id)
            return Result.ok(TokenModel.model_validate(tokens))