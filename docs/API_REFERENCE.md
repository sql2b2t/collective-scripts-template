# API скриптов Collective Client

Скрипт `.kts` выполняется в движке Kotlin (JSR-223) и получает доступ
к API клиента через глобальные переменные. Minecraft — только Mojang-маппинги:
`mc.player`, `mc.level`, `mc.gameMode`, `mc.options`.
Всё, что возвращает `ctx.*`, создается от имени скрипта и удаляется при выгрузке.

---

## Глобальные переменные

| Переменная | Тип | Описание |
|---|---|---|
| `ctx` | `ScriptContext` | модули, команды, HUD, таймеры, ротации, вывод |
| `mc` / `client` | `net.minecraft.client.Minecraft` | инстанс клиента (`player`, `level`, `gameMode`, `options`) |
| `modules` | `ModuleRegistry` | реестр модулей клиента |
| `commands` | `CommandRegistry` | реестр команд клиента |
| `friends` | `SocialRegistry` | список друзей |
| `events` | `ScriptContext.EventBridge` | шина событий (`subscribe`, `unsubscribe`, `post`) |
| `rotations` | `RotationManager` | менеджер ротаций (см. `ctx.rotate*`) |
| `baritone` | `BaritoneBridge` | мост Baritone (см. `ctx.pathTo`) |

Вывод: `ctx.print(msg)` — сообщение в чат с именем скрипта,
`ctx.log(msg)` — строка в лог клиента.
Глобальных функций `print()` и `log()` нет: эти имена перекрываются
стандартной библиотекой Kotlin, вызов `log(...)` ломает загрузку скрипта.

---

## 1. Модули и настройки

```kotlin
val mod = ctx.module("Killaura", me.collective.client.modules.ModuleCategory.COMBAT, "описание")

val range = mod.numberSetting("Range", 3.8, 1.0, 6.0, 0.1)
val playersOnly = mod.boolSetting("PlayersOnly", true)
val espColor = mod.colorSetting("Color", java.awt.Color.RED)
val mode = mod.stringSetting("Mode", "Single")
```

- `ctx.module(name, category, description)` — категория: enum `ModuleCategory`
  (COMBAT, MISC, RENDER, MOVEMENT, PLAYER, EXPLOIT, CLIENT, HUD);
  есть варианты без описания и с категорией строкой (`ctx.module("Name", "MISC")`).
- `boolSetting(name, default)` — галочка.
- `numberSetting(name, default, min, max, step)` — число; шаг не используется.
- `colorSetting(name, default)` — цвет `java.awt.Color`.
- `stringSetting(name, default)` — строка.

### Обработчики модуля

| Метод | Событие |
|---|---|
| `onEnable { mod -> }` | включение модуля |
| `onDisable { mod -> }` | выключение модуля |
| `onTick { mod -> }` | каждый тик клиента (`MinecraftTickEvent`) |
| `onTickPost { mod -> }` | тик после обновления игрока (`LocalPlayerTickEvent`) |
| `onPacketSend { event -> }` | исходящий пакет, `event.packet`, можно отменить `event.cancel()` |
| `onPacketReceive { event -> }` | входящий пакет |
| `onRender3D { event -> }` | рендер мира (`WorldRenderEvent.BeforeBufferSource`) |
| `onRender2D { graphics, delta -> }` | рендер HUD (`GuiGraphics`, доля тика) |
| `onPlayerMove { event -> }` | движение игрока (`LocalPlayerMoveEvent`) |
| `onBreakBlock { event -> }` | начало ломания блока |
| `onSendMessage { event -> }` | сообщение перед отправкой в чат |
| `onMouseClick { event -> }` | клик мыши (`InputPressEvent`) |
| `onKeyInput { event -> }` | нажатие клавиши (`InputPressEvent`) |
| `onInteractEntity { event -> }` | атака сущности (`PlayerAttackEvent`) |

Ошибки в обработчиках не роняют клиент: после 5 ошибок модуль выключается сам.

---

## 2. Элементы HUD

```kotlin
val hud = ctx.hud("MyHud", "описание", 10.0f, 10.0f)
hud.setSize(120.0f, 32.0f)

hud.onRender { poses, delta ->
    hud.drawText(poses, "текст", 2.0f, 2.0f, 0xFFFFFFFF.toInt())
}
```

- `ctx.hud(name, description, x, y)` — элемент с координатами по умолчанию.
- `setSize(width, height)` — размер в редакторе HUD.
- `onRender { poses, delta -> }` — отрисовка, `poses: PoseStack`;
  координаты относительно левого верхнего угла элемента.
- `drawText(poses, text, x, y, argb)` — текст с тенью, цвет в формате ARGB.

Настроек у HUD-элементов нет. Для отрисовки есть `drawText` — текст с тенью,
примитивы и картинки скриптовый HUD не рисует.

---

## 3. Команды чата

```kotlin
ctx.command("saycoords", "описание", { args ->
    ctx.sendChatMessage("coords " + args.joinToString(" "))
}, "alias1", "alias2")
```

- `ctx.command(name, description, action, vararg aliases)` — `action` получает
  массив аргументов после имени команды. Команда вызывается с префиксом
  команд клиента: `.saycoords`.

---

## 4. Таймеры

- `ctx.after(ticks) { ... }` — один раз через N тиков, возвращает id.
- `ctx.every(ticks) { ... }` — каждые N тиков, возвращает id.
- `ctx.cancelTask(id)` / `ctx.cancelAllTasks()` — отмена.
- 20 тиков = 1 секунда.

---

## 5. Действия и автоматизация

| Метод | Описание |
|---|---|
| `ctx.sendPacket(packet)` | отправить сырой пакет |
| `ctx.rotateSilent(yaw, pitch[, priority])` | тихий поворот для сервера |
| `ctx.rotateInstant(yaw, pitch)` | мгновенный поворот |
| `ctx.rotateTo(target)` / `ctx.rotateTo(x, y, z)` | наводка на точку (`Vec3`) |
| `ctx.pathTo(x, y, z)` / `ctx.cancelPath()` | навигация Baritone |
| `ctx.sendChatMessage(msg)` | сообщение или команда сервера |
| `ctx.executeClientCommand(cmd)` | команда клиента, например `.toggle Killaura` |
| `ctx.runOnMainThread { ... }` | выполнить код в игровом потоке |
| `ctx.getModule(name)` | найти модуль клиента по имени |

---

## 6. Часто нужное из Minecraft (Mojang-маппинги)

| Что | Где |
|---|---|
| игрок | `mc.player` (`LocalPlayer`) |
| мир | `mc.level` (`ClientLevel`) |
| атака | `mc.gameMode?.attack(player, target)` |
| свинг | `player.swing(net.minecraft.world.InteractionHand.MAIN_HAND)` |
| кулдаун удара | `player.getAttackStrengthScale(0.5f)` |
| прыжок | `player.jumpFromGround()` |
| на земле / красться | `player.onGround()` / `player.isShiftKeyDown` |
| здоровье | `player.health` |
| координаты блока | `player.blockPosition()` (`.x`, `.y`, `.z`) |
| позиция | `player.position()` (`Vec3`) |
| сущности мира | `mc.level.entitiesForRendering()` |
| живая сущность | `entity.isAlive`, рост: `entity.getBbHeight()` |
| клавиши | `mc.options.keyUp.isDown` (кнопки: keyUp, keyDown, keyLeft, keyUse) |
| текст в чате | `net.minecraft.network.chat.Component.literal("...")` |
| идентификатор ресурса | `net.minecraft.resources.Identifier` |
