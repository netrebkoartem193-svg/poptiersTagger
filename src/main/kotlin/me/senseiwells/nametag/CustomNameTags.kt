object RentryLoader {
    val tiers = mutableMapOf<String, String>()
    private var lastUpdate: Long = 0

    fun getTier(username: String): String? {
        val now = System.currentTimeMillis()
        // Обновляем список с Rentry раз в 60 секунд
        if (now - lastUpdate > 60000) {
            lastUpdate = now
            Thread {
                try {
                    val url = java.net.URL("https://rentry.co/твой_хеш/raw")
                    val text = url.readText()
                    tiers.clear()
                    text.lines().forEach { line ->
                        val parts = line.split(":")
                        if (parts.size == 2) {
                            tiers[parts[0].trim().lowercase()] = parts[1].trim()
                        }
                    }
                } catch (_: Exception) {}
            }.start()
        }
        return tiers[username.lowercase()]
    }
}
