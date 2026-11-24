// PacketLogger.kts — имена отправленных и полученных пакетов в лог клиента.
// Лог видно в консоли игры (не в чате).

import me.collective.client.modules.ModuleCategory

val packetMod = ctx.module("PacketLogger", ModuleCategory.MISC, "Пишет имена пакетов в лог")

packetMod.onPacketSend { event ->
    val name = event.packet.javaClass.simpleName
    // пакеты движения слишком частые, их не логируем
    if (!name.contains("Move")) {
        ctx.log("[SEND] " + name)
    }
}

packetMod.onPacketReceive { event ->
    val name = event.packet.javaClass.simpleName
    if (!name.contains("Chunk")) {
        ctx.log("[RECV] " + name)
    }
}

ctx.print("PacketLogger загружен")