from typing import Literal

from fastapi import HTTPException
from pydantic import BaseModel
from starlette import status

EXC_TYPES = Literal["unique_failed", "forbidden", "unauthorized", "invalid", "other", "not_found"]

class ExceptionDetails(BaseModel):
    type: EXC_TYPES
    loc: list[str]
    msg: str | None = None


class ExceptionModel(BaseModel):
    details: ExceptionDetails


def raise_exception(err_type: EXC_TYPES, msg: str, loc: list[str] = None):
    detail = ExceptionDetails(
        type=err_type,
        loc=loc or ["server"],
        msg=msg
    )
    raise HTTPException(
        status_code=get_status_code(err_type),
        detail=detail.model_dump()
    )

def get_status_code(err_type: EXC_TYPES) -> int:
    mapping = {
        "unique_failed": status.HTTP_400_BAD_REQUEST,
        "unauthorized": status.HTTP_401_UNAUTHORIZED,
        "forbidden": status.HTTP_403_FORBIDDEN,
        "not_found": status.HTTP_404_NOT_FOUND,
        "invalid": status.HTTP_422_UNPROCESSABLE_ENTITY,
    }
    return mapping.get(err_type, 500)