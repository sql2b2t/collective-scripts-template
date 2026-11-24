// HealthNotifier.kts — предупреждение в чат при низком здоровье.

import me.collective.client.modules.ModuleCategory

val healthMod = ctx.module("HealthNotifier", ModuleCategory.COMBAT, "Предупреждает о низком здоровье")

var lastAlert = 0L

healthMod.onTick { mod ->
    val player = mc.player ?: return@onTick
    val health = player.health
    val now = System.currentTimeMillis()
    if (health <= 6.0f && now - lastAlert > 3000L) {
        lastAlert = now
        ctx.print("§cЗдоровье низкое: " + String.format("%.1f", health) + " HP")
    }
}

ctx.print("HealthNotifier загружен")