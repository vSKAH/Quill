# Quill
![Version](https://reposilite.skworks.tech/api/badge/latest/snapshots/tech/sk-works/hytale/Quill?color=orange&name=Quill&prefix=v) ![Java](https://img.shields.io/badge/Java-25-ED8B00?style=flat-square&logo=openjdk&logoColor=white) [![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=vSKAH_Quill&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=vSKAH_Quill) ![License](https://img.shields.io/badge/license-LGPL%20v3-blue?style=flat-square)

A lightweight Java framework for Hytale servers providing rich text formatting
(colors, gradients, bold, italic…) and a flexible multi-language translation system.

---

## Installation

**Maven**
```Xml
<repositories>
    <repository>
      <id>sk-works-releases</id>
      <url>https://reposilite.sk-works.tech/releases</url>
    </repository>

    <repository>
      <id>sk-works-snapshots</id>
      <url>https://reposilite.sk-works.tech/snapshots</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
      <groupId>tech.sk-works.hytale</groupId>
      <artifactId>Quill</artifactId>
      <version>1.0-SNAPSHOT</version>
    </dependency>
</dependencies>
```

**Gradle (Kotlin DSL)**
```Kotlin
maven("https://reposilite.sk-works.tech/releases")
maven("https://reposilite.sk-works.tech/snapshots")

implementation("tech.sk-works.hytale:Quill:1.0-SNAPSHOT")
```

---

## Features

- **Rich text formatting** — colors, gradients, bold, italic, underline, monospace via simple tags
- **i18n / translations** — multi-language cache system with hot-reload support
- **Fluent builder API** — type-safe param injection with missing-param detection
- **Optimized broadcast** — messages resolved once per language, not once per player
- **Extensible** — bring your own `Translation` enum, just implement `Translatable`

---

## Quick Start

### Define your translations

```java
public enum Translation implements Translatable {

    BOUNTY_PLACED("commands.bounty.placed.broadcast"),
    BOUNTY_EMPTY_HAND("commands.bounty.place.empty-hand");

    static {
        TranslationService.register(values());
    }

    private final String messageId;
    Translation(String id) {
        this.messageId = id;
    }

    @Override public String getMessageId() {
        return messageId;
    }

    public Message get(PlayerRef ref) {
        return TranslationService.get(this, ref);
    }

    public TranslationBuilder builder(PlayerRef ref) {
        return TranslationService.builder(this, ref);
    }
}
```


### i18n files


```properties
#Path: resources/Server/Languages/en-US/commands.lang ("commands" for exemple but you can use the name as you want)
bounty.placed.broadcast=<gradient:#FF4444:#FF8800>{hunter}</gradient> placed a bounty of <gold>{amount}g</gold> on <white>{target}</white>!
bounty.place.empty-hand=<red>You must hold an item in your hand.
```


### Send a message


```java
    // Simple — no params, served from cache
    Translation.BOUNTY_EMPTY_HAND.get(playerRef);

    // With params
    Translation.BOUNTY_PLACED
        .builder(playerRef)
        .param("hunter", "vSKAH")
        .param("target", "Notch")
        .param("amount", 1000)
        .send(playerRef);

    // Broadcast — resolved once per language
    Translation.BOUNTY_PLACED
        .builder((PlayerRef) null)
        .param("hunter", "vSKAH")
        .param("target", "Notch")
        .param("amount", 1000)
        .broadcast(server.getOnlinePlayers());
```


### Cache invalidation

```java
    TranslationService.invalidateAll();       // full reload
    TranslationService.invalidate("fr-FR");   // single language
```

---

### Using without translations system

```java
    ParsedMessage msg = Quill.parse("<gradient:#FF0000:#0000FF>Hello <bold>world</bold>!</gradient>");

    msg.getRawInput();    // "<gradient:#FF0000:#0000FF>Hello <bold>world</bold>!</gradient>"
    msg.getPlainText();   // "Hello world!"
    msg.hasColors();      // true
    msg.getResult();      // Message ready to send
```
