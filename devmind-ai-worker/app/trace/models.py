from dataclasses import dataclass
from enum import Enum


class ProcessingStatus(str, Enum):
    UPLOADED = "UPLOADED"
    PARSING = "PARSING"
    EMBEDDING = "EMBEDDING"
    COMPLETED = "COMPLETED"
    FAILED = "FAILED"


@dataclass(frozen=True)
class TraceRecord:
    agent_name: str
    step_name: str
    status: str
    latency_ms: int
    message: str
