from typing import Annotated
from fastapi import APIRouter, BackgroundTasks, Depends, Response
from fastapi.security import OAuth2PasswordRequestForm

from backend.src.core import security
from backend.src.core.notifications import send_verification_email
from backend.src.dependencies.models.user import CurrentUser
from backend.src.dependencies.services.auth import AuthServiceDep
from backend.src.schemas.auth import ResendCode, TokenModel, UserLogin, VerifyEmailRequest
from backend.src.schemas.exception import ExceptionModel, raise_exception
from backend.src.schemas.user import UserCreate, UserRead
from backend.src.schemas.result import ErrorCode

router = APIRouter(prefix = "/auth", tags = ["Auth"])

@router.post("/register", 
             status_code = 201,
             response_model = UserRead,
             responses = {400: {"model": ExceptionModel, "description": "Email already exists"}}
             )
async def register(body: UserCreate, auth_service: AuthServiceDep, background_tasks: BackgroundTasks):
    result = await auth_service.register(body)
    if not result.is_success:
        msg = result.error.message
        raise_exception(
            msg=msg,
            err_type = "unique_failed",
            loc = ["body", "email"]
        )
    else:
        val = result.value
        if val[1] != "ALREADY_SENT":
            background_tasks.add_task(send_verification_email, body.email, body.name, val[1])

        return val[0]
        

@router.post("/verify_registration",
             status_code=200,
             response_model = UserRead,
             responses={
                 400: {"model": ExceptionModel, "description": "Invalid code"},
                 403: {"model": ExceptionModel, "description": "Forbidden (user not found)"},
                 404: {"model": ExceptionModel, "description": "No active codes(try resending)"}
             })
async def verify_registration(body: VerifyEmailRequest, auth_service: AuthServiceDep):
    result = await auth_service.verify_register(body.user_id, body.code)
    if not result.is_success:
        error = result.error
        msg = error.message
        match error.error_code:
            case ErrorCode.INVALID_CODE:
                raise_exception(
                    msg = msg,
                    loc = ["body", "code"],
                    err_type = "invalid"
                )

            case ErrorCode.NO_ALIVE_CODES:
                raise_exception(
                    msg = msg,
                    loc = ["body", "code"],
                    err_type = "not_found"
                )

            case ErrorCode.INVALID_CREDENTIALS:
                raise_exception(
                    msg = msg,
                    loc = ["body", "user_id"],
                    err_type = "forbidden"
                )
    
    return result.value


@router.post(
        "/resend_verification_code", 
        description = "If email was resend, will return 201, otherwise - 200",
        responses = {
            201: {"description": "Code was sent"},
            200: {"description": "Accepted"}
        })
async def resend_verification(
    body: ResendCode,
    background_tasks: BackgroundTasks,
    auth_service: AuthServiceDep
    ):
    result = await auth_service.resend_verification_code(body.user_id)
    if not result.is_success:
        return Response(status_code = 200)
    
    user, code = result.value
    if code != "ALREADY_SENT":
        background_tasks.add_task(
            send_verification_email, 
            user.email, 
            user.name, 
            code
        )
        return Response(status_code = 201)
    
    return Response(status_code = 200)


@router.post(
    "/login", 
    response_model = TokenModel,
    responses = {
        401: {"model": ExceptionModel, "description": "Invalid credentials"},
        403: {"model": ExceptionModel, "description": "Forbidden(most likely not verified)"}
        }
             )
async def login(body: Annotated[OAuth2PasswordRequestForm, Depends()], auth_service: AuthServiceDep):
    login_body = UserLogin(
        email = body.username,
        password = body.password
    )
    result = await auth_service.login(login_body)
    if not result.is_success:
        msg = result.error.message
        match result.error.error_code:
            case ErrorCode.FORBIDDEN:
                raise_exception(
                    err_type = "forbidden",
                    msg = msg,
                    loc = ["body"]
                )
            case _:
                raise_exception(
                    err_type = "unauthorized",
                    msg = result.error.message,
                    loc = ["body"]
                )

    return result.value


@router.post(
        "/refresh_token", 
        response_model = TokenModel,
        responses = {
        401: {"model": ExceptionModel, "description": "Invalid credentials"},
        403: {"model": ExceptionModel, "description": "Forbidden(most likely not verified)"}
        })
async def refresh_token(user: CurrentUser):
    tokens = security.Token.create_token(user_id = user.id)
    return TokenModel.model_validate(tokens)


@router.post(
    "/me",
    response_model = UserRead,
    responses = {
        401: {"model": ExceptionModel, "description": "Invalid credentials"},
        403: {"model": ExceptionModel, "description": "Forbidden(most likely not verified)"}
    }
    )
async def profile(user: CurrentUser):
    return user