// CustomRadarHUD.kts — HUD со счётчиком сущностей и команда .saycoords.

val radar = ctx.hud("CustomRadarHUD", "Счётчик сущностей рядом", 10.0f, 150.0f)
radar.setSize(120.0f, 32.0f)

radar.onRender { poses, delta ->
    val player = mc.player ?: return@onRender
    val level = mc.level ?: return@onRender

    var count = 0
    for (entity in level.entitiesForRendering()) {
        if (entity != player && entity.isAlive) count++
    }

    radar.drawText(poses, "Radar", 2.0f, 2.0f, 0xFFFFFFFF.toInt())
    radar.drawText(poses, "Entities: " + count, 2.0f, 18.0f, 0xFFCCCCCC.toInt())
}

// своя команда чата: .saycoords (алиас .sc)
ctx.command("saycoords", "Пишет координаты в чат", { args ->
    val player = mc.player ?: return@command
    val pos = player.blockPosition()
    ctx.sendChatMessage("coords " + pos.x + " " + pos.y + " " + pos.z)
}, "sc")

ctx.print("CustomRadarHUD загружен")