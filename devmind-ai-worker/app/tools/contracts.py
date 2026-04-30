from dataclasses import dataclass


@dataclass(frozen=True)
class ToolRequest:
    tool_name: str
    actor: str
    permission_scope: str
    payload: dict[str, object]


@dataclass(frozen=True)
class ToolResult:
    allowed: bool
    tool_name: str
    replay_token: str
    message: str


def authorize_tool_request(request: ToolRequest, allowed_scopes: set[str]) -> ToolResult:
    allowed = request.permission_scope in allowed_scopes
    return ToolResult(
        allowed=allowed,
        tool_name=request.tool_name,
        replay_token=f"{request.actor}:{request.tool_name}",
        message="authorized" if allowed else "forbidden"
    )
