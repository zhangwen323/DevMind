from app.providers.base import ChatRequest, ChatResponse, EmbeddingRequest, EmbeddingResponse, LlmProvider


class ProviderRegistry:
    def __init__(self) -> None:
        self._providers: dict[str, LlmProvider] = {}

    def register(self, provider: LlmProvider) -> None:
        self._providers[provider.name] = provider

    def get(self, name: str) -> LlmProvider:
        try:
            return self._providers[name]
        except KeyError as exc:
            raise ValueError(f"Unsupported provider: {name}") from exc


class QwenProvider:
    name = "qwen"

    def chat(self, request: ChatRequest) -> ChatResponse:
        return ChatResponse(provider=self.name, content=f"stubbed:{request.prompt}")

    def embed(self, request: EmbeddingRequest) -> EmbeddingResponse:
        return EmbeddingResponse(provider=self.name, vector=[0.1, 0.2, 0.3])


class OpenAiProvider:
    name = "openai"

    def chat(self, request: ChatRequest) -> ChatResponse:
        return ChatResponse(provider=self.name, content=f"stubbed:{request.prompt}")

    def embed(self, request: EmbeddingRequest) -> EmbeddingResponse:
        return EmbeddingResponse(provider=self.name, vector=[0.4, 0.5, 0.6])
