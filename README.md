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
        <version>v1.0</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
