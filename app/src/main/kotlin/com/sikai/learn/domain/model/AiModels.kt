package com.sikai.learn.domain.model

enum class AiCapability { TEXT, VISION, FILE_UPLOAD, PDF, STREAMING }

enum class AiProviderType(val displayName: String, val defaultCapabilities: List<AiCapability>) {
    QWEN("Qwen", listOf(AiCapability.TEXT, AiCapability.VISION, AiCapability.FILE_UPLOAD, AiCapability.STREAMING)),
    DEEPINFRA("DeepInfra", listOf(AiCapability.TEXT, AiCapability.VISION)),
    GEMINI("Gemini", listOf(AiCapability.TEXT, AiCapability.VISION, AiCapability.FILE_UPLOAD, AiCapability.PDF, AiCapability.STREAMING)),
    OPENROUTER("OpenRouter", listOf(AiCapability.TEXT, AiCapability.VISION, AiCapability.FILE_UPLOAD)),
    NVIDIA("NVIDIA", listOf(AiCapability.TEXT, AiCapability.VISION)),
    DEEPSEEK("DeepSeek", listOf(AiCapability.TEXT, AiCapability.STREAMING)),
    CUSTOM("Custom", listOf(AiCapability.TEXT))
}

data class AiModel(
    val id: String,
    val name: String,
    val capabilities: List<AiCapability>
)

data class AiProviderConfig(
    val id: String,
    val type: AiProviderType,
    val name: String,
    val baseUrl: String,
    val apiKey: String? = null,
    val textModel: String,
    val multimodalModel: String? = null,
    val customCapabilities: List<AiCapability>? = null,
    val requestFormat: RequestFormat = RequestFormat.OPENAI_COMPATIBLE,
    val isEnabled: Boolean = true,
    val priority: Int = 0
) {
    val capabilities: List<AiCapability>
        get() = customCapabilities ?: type.defaultCapabilities
}

enum class RequestFormat {
    OPENAI_COMPATIBLE,
    GEMINI_COMPATIBLE,
    CUSTOM_SIMPLE
}

enum class AiFailureReason {
    RATE_LIMIT, TIMEOUT, SERVER_ERROR, INVALID_API_KEY, MODEL_UNAVAILABLE,
    NETWORK_ERROR, PARSING_ERROR, UNSUPPORTED_CAPABILITY, UNKNOWN
}

data class AiMessage(
    val role: String, // system, user, assistant
    val content: String,
    val attachments: List<AiAttachment> = emptyList()
)

data class AiAttachment(
    val mimeType: String,
    val base64Data: String? = null,
    val fileUrl: String? = null,
    val fileName: String? = null
)

data class AiRequest(
    val messages: List<AiMessage>,
    val model: String,
    val temperature: Float = 0.7f,
    val maxTokens: Int = 2048,
    val stream: Boolean = false
)

sealed class AiResponse {
    data class Success(
        val text: String,
        val modelUsed: String,
        val providerId: String,
        val tokensUsed: Int? = null
    ) : AiResponse()
    data class Failure(
        val reason: AiFailureReason,
        val message: String,
        val providerId: String? = null
    ) : AiResponse()
}

sealed class ProviderHealthState {
    data object Healthy : ProviderHealthState()
    data class Unhealthy(val reason: String) : ProviderHealthState()
    data object Unknown : ProviderHealthState()
}
