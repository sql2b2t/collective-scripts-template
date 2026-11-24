# Collective Client — шаблон скриптов на Kotlin

collective client - клиент для копий 2b2t (Minecraft 1.21.11, Fabric).

Скрипты `.kts` работают во встроенном скриптовом движке клиента.
Скрипт регистрирует модули, команды и элементы HUD: они появляются в ClickGUI,
сохраняются в конфиг и выгружаются без следа. Правки применяются без
перезапуска игры: `.script reload`.

## Структура

```text
collective-scripts-template/
├── docs/
│   └── API_REFERENCE.md      описание API движка
├── scripts/
│   ├── AutoJump.kts          прыжок при движении вперёд
│   ├── PacketLogger.kts      имена пакетов в лог клиента
│   ├── HealthNotifier.kts    предупреждение при низком здоровье
│   ├── CoordNotifier.kts     координаты раз в 10 секунд
│   ├── CombatAssistant.kts   наводка и атака ближайшей цели
│   ├── CustomRadarHUD.kts    HUD со счётчиком сущностей и команда .saycoords
│   └── ImageHUD.kts          простой HUD на drawText
└── README.md
```

## Установка

1. Открой папку скриптов командой `.script folder`
   (или вручную: `.minecraft/collective/scripts/`).
2. Скопируй нужные `.kts` из папки `scripts/` в эту папку.
3. Выполни `.script reload`.
4. Включи модуль в ClickGUI (правый Shift по умолчанию).

## Команды

| Команда | Что делает |
|---|---|
| `.script list` | список скриптов и ошибки загрузки |
| `.script load <имя>` | загрузить скрипт |
| `.script unload <имя>` | выгрузить скрипт вместе с его модулями и командами |
| `.script reload` | перезагрузить все скрипты |
| `.script delete <имя>` | удалить файл скрипта |
| `.script examples` | создать встроенные примеры в папке скриптов |
| `.script folder` | открыть папку скриптов |

## Примеры

### Модуль с тиком (AutoJump.kts)

```kotlin
import me.collective.client.modules.ModuleCategory

val jumpMod = ctx.module("AutoJump", ModuleCategory.MOVEMENT, "Прыгает при движении вперёд")

jumpMod.onTick { mod ->
    val player = mc.player ?: return@onTick
    if (player.onGround() && mc.options.keyUp.isDown && !player.isShiftKeyDown) {
        player.jumpFromGround()
    }
}
```

### Перехват пакетов (PacketLogger.kts)

```kotlin
import me.collective.client.modules.ModuleCategory

val packetMod = ctx.module("PacketLogger", ModuleCategory.MISC, "Пишет имена пакетов в лог")

packetMod.onPacketSend { event ->
    ctx.log("[SEND] " + event.packet.javaClass.simpleName)
}
```

Про вывод: пиши в чат через `ctx.print(...)`, в лог клиента через `ctx.log(...)`.
Глобальных функций `print()` и `log()` в скриптах нет: эти имена перекрываются
стандартной библиотекой Kotlin, и вызов `log(...)` вообще не даст скрипту
загрузиться.

## API

Все классы, методы и события: [docs/API_REFERENCE.md](docs/API_REFERENCE.md).

## Лицензия

MIT.
