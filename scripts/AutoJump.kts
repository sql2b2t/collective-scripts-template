// AutoJump.kts — прыжок при движении вперёд.
// Модуль появится в ClickGUI в категории MOVEMENT.

import me.collective.client.modules.ModuleCategory

val jumpMod = ctx.module("AutoJump", ModuleCategory.MOVEMENT, "Прыгает при движении вперёд")

jumpMod.onEnable { mod ->
    ctx.print("AutoJump включен")
}

jumpMod.onDisable { mod ->
    ctx.print("AutoJump выключен")
}

jumpMod.onTick { mod ->
    val player = mc.player ?: return@onTick
    if (player.onGround() && mc.options.keyUp.isDown && !player.isShiftKeyDown) {
        player.jumpFromGround()
    }
}

ctx.print("AutoJump загружен")