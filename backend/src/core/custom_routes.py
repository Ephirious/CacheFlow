from functools import wraps

from backend.src.schemas.exception import ExceptionModel

def protected_route(router_method, *args, **kwargs):
    extra_responses = {
        401: {"model": ExceptionModel, "description": "Invalid or expired token"},
        403: {"model": ExceptionModel, "description": "Not verified or insufficient permissions"}
    }
    kwargs["responses"] = {**kwargs.get("responses", {}), **extra_responses}
    
    return router_method(*args, **kwargs)