package com.example.model

enum class ModelTier(
    val id: String,
    val label: String,
    val desc: String,
    val isPro: Boolean
) {
    S_TURBO_FLASH("s-turbo-flash", "S TURBO FLASH", "Fastest, free", false),
    S_TURBO_HIGH("s-turbo-high", "S TURBO HIGH", "Balanced, free", false),
    S_TURBO_MAX("s-turbo-max", "S TURBO MAX", "Free, limited (20 msg / 8h)", false),
    S_TURBO_ULTRA_MAX("s-turbo-ultra-max", "S TURBO ULTRA MAX", "Earth & human history, elite quality", true);

    companion object {
        fun fromId(id: String): ModelTier {
            return entries.find { it.id == id } ?: S_TURBO_FLASH
        }
    }
}

enum class SpecialistPersona(
    val id: String,
    val title: String,
    val description: String,
    val systemPromptAddon: String
) {
    GENERAL(
        id = "general",
        title = "S TURBO العام",
        description = "الذكاء الفائق الشامل لجميع المهام اليومية والمعقدة",
        systemPromptAddon = "You are in GENERAL SUPERINTELLIGENCE mode. Provide the most optimal, comprehensive, and well-rounded assistance."
    ),
    REASONING(
        id = "reasoning",
        title = "التفكير العميق والمنطق",
        description = "نماذج التفكير الاستدلالي لحل أعقد المسائل الرياضية والبرمجية",
        systemPromptAddon = "You are in DEEP REASONING mode (equivalent to o1/o1-mini). First systematically break down the problem into logical steps, evaluate assumptions, and reason thoroughly before providing the final definitive solution."
    ),
    CODER(
        id = "coder",
        title = "مهندس البرمجيات والأكواد",
        description = "كتابة الأكواد، اكتشاف الأخطاء وتصميم البنى الهندسية",
        systemPromptAddon = "You are in PRINCIPAL SOFTWARE ARCHITECT mode. Write clean, modular, production-ready code with complete implementations. Provide debugging steps, optimal time/space complexity, and architecture best practices."
    ),
    DATA_ANALYST(
        id = "data",
        title = "محلل البيانات والإحصاء",
        description = "تحليل ملفات CSV/Excel واستخراج الرؤى والمؤشرات البيانية",
        systemPromptAddon = "You are in ADVANCED DATA ANALYST mode. Focus on mathematical precision, statistical metrics, trend analysis, tabular summaries, and concrete analytical takeaways."
    ),
    WEB_RESEARCHER(
        id = "research",
        title = "الباحث المعرفي المباشر",
        description = "البحث في الويب والوصول إلى أحدث الأخبار وتوثيق المصادر",
        systemPromptAddon = "You are in REAL-TIME RESEARCH & GROUNDING mode. Strictly synthesize live factual data, cross-reference multiple angles, and cite credible sources clearly."
    ),
    CREATIVE_WRITER(
        id = "writer",
        title = "الكاتب والمحرر الإبداعي",
        description = "صياغة نصوص أدبية، مقالات مقنعة ومحتوى تسويقي راقٍ",
        systemPromptAddon = "You are in CREATIVE SCRIBE mode. Craft eloquent, evocative, and persuasive prose with rich vocabulary, natural cadence, and compelling narrative flow."
    ),
    CYBER_SEC(
        id = "cybersec",
        title = "خبير الأمن السيبراني",
        description = "تدقيق حماية الأنظمة واكتشاف الثغرات وتشفير البيانات",
        systemPromptAddon = "You are in CYBERSECURITY SENTINEL mode. Analyze security posture, threat vectors, cryptographic best practices, and secure code hardening."
    );

    companion object {
        fun fromId(id: String?): SpecialistPersona {
            return entries.find { it.id == id } ?: GENERAL
        }
    }
}

enum class MessageRole {
    USER, MODEL
}

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val role: MessageRole,
    val text: String,
    val streaming: Boolean = false,
    val timestamp: Long = System.currentTimeMillis(),
    val imagePrompt: String? = null,
    val imageUrls: List<String> = emptyList(),
    val mediaDataUri: String? = null,
    val mediaType: String? = null,
    val mediaName: String? = null,
    val isSearching: Boolean = false,
    val thinkingText: String? = null,
    val thinkingDurationSeconds: Double? = null,
    val searchSources: List<WebSearchResult> = emptyList(),
    val personaId: String? = null,
    val docName: String? = null
)

data class ChatSession(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val createdAt: Long = System.currentTimeMillis(),
    val messages: List<ChatMessage> = emptyList()
)

data class UserMemory(
    val name: String? = null,
    val aboutUser: String? = null,
    val responseInstructions: String? = null,
    val facts: List<String> = emptyList(),
    val preferences: List<String> = emptyList()
)

data class WebSearchResult(
    val title: String,
    val snippet: String,
    val url: String
)

