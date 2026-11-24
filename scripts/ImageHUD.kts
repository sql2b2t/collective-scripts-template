// ImageHUD.kts — простой HUD с информацией об игроке.
// Раньше здесь была загрузка картинок; сейчас это минимальный пример HUD на drawText.

val infoHud = ctx.hud("ImageHUD", "Координаты и здоровье на экране", 10.0f, 60.0f)
infoHud.setSize(170.0f, 48.0f)

infoHud.onRender { poses, delta ->
    val player = mc.player ?: return@onRender
    val pos = player.blockPosition()

    infoHud.drawText(poses, "ImageHUD", 2.0f, 2.0f, 0xFFFFFFFF.toInt())
    infoHud.drawText(poses, "XYZ: " + pos.x + " " + pos.y + " " + pos.z, 2.0f, 18.0f, 0xFFB0B0B0.toInt())
    infoHud.drawText(poses, "HP: " + String.format("%.1f", player.health), 2.0f, 34.0f, 0xFFFF6666.toInt())
}

ctx.print("ImageHUD загружен")