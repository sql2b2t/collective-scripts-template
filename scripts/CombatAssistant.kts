// CombatAssistant.kts — наводка и атака ближайшей цели.

import me.collective.client.modules.ModuleCategory
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player

val combat = ctx.module("CombatAssistant", ModuleCategory.COMBAT, "Наводится и атакует ближайшую цель")

// настройки видны в ClickGUI
val rangeSetting = combat.numberSetting("Range", 4.2, 1.0, 6.0, 0.1)
val playersOnly = combat.boolSetting("PlayersOnly", true)

combat.onTick { mod ->
    val player = mc.player ?: return@onTick
    val level = mc.level ?: return@onTick

    // ближайшая живая цель в радиусе
    var target: Entity? = null
    var best = rangeSetting.value
    for (entity in level.entitiesForRendering()) {
        if (entity == player || !entity.isAlive) continue
        if (playersOnly.value && entity !is Player) continue
        val dist = player.distanceTo(entity).toDouble()
        if (dist <= best) {
            best = dist
            target = entity
        }
    }

    val goal = target ?: return@onTick

    // тихий поворот на цель
    ctx.rotateTo(goal.position().add(0.0, goal.getBbHeight() * 0.75, 0.0))

    // атака после восстановления кулдауна
    if (player.getAttackStrengthScale(0.5f) >= 1.0f) {
        mc.gameMode?.attack(player, goal)
        player.swing(InteractionHand.MAIN_HAND)
    }
}

ctx.print("CombatAssistant загружен")