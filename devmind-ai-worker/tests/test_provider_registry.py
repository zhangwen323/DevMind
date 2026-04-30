import pytest

from app.providers.base import ChatRequest
from app.providers.registry import OpenAiProvider, ProviderRegistry, QwenProvider
from app.tools.contracts import ToolRequest, authorize_tool_request


def test_registry_returns_registered_provider() -> None:
    registry = ProviderRegistry()
    registry.register(QwenProvider())
    registry.register(OpenAiProvider())

    provider = registry.get("qwen")
    response = provider.chat(ChatRequest(prompt="hello", provider="qwen"))

    assert response.provider == "qwen"
    assert response.content == "stubbed:hello"


def test_registry_rejects_unknown_provider() -> None:
    registry = ProviderRegistry()

    with pytest.raises(ValueError):
        registry.get("missing")


def test_tool_authorization_respects_scope() -> None:
    request = ToolRequest(
        tool_name="sql.query",
        actor="router-agent",
        permission_scope="sql:read",
        payload={}
    )

    allowed = authorize_tool_request(request, {"sql:read"})
    denied = authorize_tool_request(request, {"rag:read"})

    assert allowed.allowed is True
    assert denied.allowed is False
