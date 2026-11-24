// CoordNotifier.kts — координаты игрока в чат раз в 10 секунд.

import me.collective.client.modules.ModuleCategory

val coordMod = ctx.module("CoordNotifier", ModuleCategory.RENDER, "Пишет координаты раз в 10 секунд")

var lastReport = 0L

coordMod.onTick { mod ->
    val player = mc.player ?: return@onTick
    val now = System.currentTimeMillis()
    if (now - lastReport > 10000L) {
        lastReport = now
        val pos = player.blockPosition()
        ctx.print("§7Координаты: §b" + pos.x + " " + pos.y + " " + pos.z)
    }
}

ctx.print("CoordNotifier загружен")