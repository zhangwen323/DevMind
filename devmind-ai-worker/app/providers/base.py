from dataclasses import dataclass
from typing import Protocol


@dataclass(frozen=True)
class ChatRequest:
    prompt: str
    provider: str


@dataclass(frozen=True)
class ChatResponse:
    provider: str
    content: str


@dataclass(frozen=True)
class EmbeddingRequest:
    text: str
    provider: str


@dataclass(frozen=True)
class EmbeddingResponse:
    provider: str
    vector: list[float]


class LlmProvider(Protocol):
    name: str

    def chat(self, request: ChatRequest) -> ChatResponse:
        ...

    def embed(self, request: EmbeddingRequest) -> EmbeddingResponse:
        ...
