# PandyNickColor

PandyNickColor — это плагин для Minecraft-серверов на Paper, который позволяет управлять цветами ника игроков в TAB-листе. Плагин предоставляет API для разработчиков, чтобы интегрировать управление цветами в свои проекты.

## Установка
1. Скачайте последнюю версию плагина из [релизов](https://github.com/RazerAtheriez/PandyNickColor/releases).
2. Поместите JAR-файл в папку `plugins` вашего сервера.
3. Перезапустите сервер.

## Зависимости
- **Paper**: 1.21.4 или новее.
- **TAB**: 5.2.0 или новее (доступен на [GitHub NEZNAMY/TAB](https://github.com/NEZNAMY/TAB)).

## Использование API

### Добавление зависимости
Добавьте PandyNickColor как зависимость в ваш `pom.xml` через JitPack:

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.RazerAtheriez</groupId>
        <artifactId>PandyNickColor</artifactId>
        <version>v1.3</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

### Получение доступа к API
Для использования API в вашем плагине, получите экземпляр API следующим образом:

```java
import me.pandy.PandyNickColor;
import me.pandy.api.PandyNickColorAPI;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

// В вашем методе или классе
Plugin pandyPlugin = Bukkit.getPluginManager().getPlugin("PandyNickColor");
if (pandyPlugin instanceof PandyNickColor) {
    PandyNickColorAPI api = ((PandyNickColor) pandyPlugin).getAPI();
    // Теперь вы можете использовать методы API
}
```

### Основные методы API

#### Установка цвета ника игрока
```java
// Устанавливает цвет ника игрока по ключу цвета из конфигурации
api.setNickColor(player, "red");
```

#### Сброс цвета ника
```java
// Сбрасывает цвет ника игрока к стандартному
api.resetNickColor(player);
```

#### Проверка наличия цвета у игрока
```java
// Проверяет, имеет ли игрок доступ к указанному цвету
boolean hasColor = api.hasNickColor(player, "blue");
```

#### Получение доступных цветов для игрока
```java
// Возвращает список ключей цветов, доступных игроку
List<String> availableColors = api.getAvailableColors(player);
```

#### Получение всех цветов из конфигурации
```java
// Возвращает список всех ключей цветов из конфигурации
List<String> allColors = api.getAllColors();
```

#### Получение описания цвета
```java
// Возвращает описание цвета по его ключу
String description = api.getColorDescription("green");
```

#### Удаление цвета у игрока
```java
// Удаляет указанный цвет из списка доступных игроку
api.removeNickColor(player, "yellow");
```

### Пример использования

Пример плагина, который дает игроку красный цвет ника при входе на сервер:

```java
import me.pandy.PandyNickColor;
import me.pandy.api.PandyNickColorAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class ExamplePlugin extends JavaPlugin implements Listener {

    private PandyNickColorAPI pandyAPI;

    @Override
    public void onEnable() {
        // Проверяем наличие плагина PandyNickColor
        Plugin pandyPlugin = Bukkit.getPluginManager().getPlugin("PandyNickColor");
        if (pandyPlugin instanceof PandyNickColor) {
            pandyAPI = ((PandyNickColor) pandyPlugin).getAPI();
            getServer().getPluginManager().registerEvents(this, this);
            getLogger().info("Успешно подключен к PandyNickColor API!");
        } else {
            getLogger().warning("PandyNickColor не найден! Плагин будет отключен.");
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (pandyAPI != null) {
            Player player = event.getPlayer();
            // Даем игроку красный цвет ника при входе
            pandyAPI.setNickColor(player, "red");
            player.sendMessage("§aВам установлен красный цвет ника!");
        }
    }
}
```
