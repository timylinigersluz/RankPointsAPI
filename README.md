# RankPointsAPI

**RankPointsAPI** ist eine leichtgewichtige und flexible Java-API, um Spielerpunkte in einem verteilten Minecraft-Servernetzwerk (Velocity + Paper-Server) zentral in einer **MySQL-Datenbank** zu verwalten.  
Die API stellt einfache Methoden bereit, um Punkte anhand der Spieler-UUID zu **lesen**, **setzen** und **hinzuzufügen**.  
Zusätzlich gibt es einen **konfigurierbaren Staff-Ausschluss**: Entwickler können festlegen, ob Staff-Mitglieder Punkte sammeln dürfen oder nicht.  
Neu unterstützt die API auch **AFK-Erkennung über EssentialsX**, sodass keine Punkte vergeben werden, wenn Spieler AFK sind (via Proxy-Sync).

---

## 💡 Anwendungsfall (Use Case)

- Alle Punkte (z. B. aus SMP, Minigames oder Proxy-Spielzeit) werden **global synchronisiert**.
- Typische Punktequellen:
    - SMP: Blockabbau/-platzierung, Advancements, Endboss-Kills
    - Proxy: Spielzeit (z. B. 1 Punkt pro Minute)
    - Minigames: Siege, Platzierungen, Rekorde
- Staff-Mitglieder (Owner, Admins, Mods) stehen in einer separaten `stafflist`.
- EssentialsX auf Paper-Servern meldet automatisch den AFK-Status an den Proxy.

**Neu:**
- Optionaler EssentialsX-Hook: sendet AFK-Status automatisch an das RankProxyPlugin (`rankproxy:afk`).
- AFK-Spieler erhalten keine Punkte, bis sie wieder aktiv sind.

---

## ✅ Features

- MySQL-basierte Speicherung (`points` + `stafflist`)
- Automatische Tabellenerstellung
- Staff-Ausschluss optional (`excludeStaff = true/false`)
- Sicher dank `PreparedStatement` und `ON DUPLICATE KEY`
- Kompatibel mit Velocity, Bukkit und Paper
- Stabil durch HikariCP-Connection-Pooling
- AFK-Bridge mit EssentialsX (automatisch aktiviert, falls installiert)

---

## 📦 Installation in dein Plugin

### Schritt 1: JitPack-Repository hinzufügen
```xml
<repository>
  <id>jitpack.io</id>
  <url>https://jitpack.io</url>
</repository>
```

### Schritt 2: Dependency einbinden (vX.X.X durch Version ersetzen)
```xml
<dependency>
  <groupId>com.github.timylinigersluz</groupId>
  <artifactId>RankPointsAPI</artifactId>
  <version>vX.X.X</version>
</dependency>
```

### Schritt 3: EssentialsX-Repository (für AFK-Funktion, optional)
```xml
<repository>
  <id>essentialsx-repo</id>
  <url>https://repo.essentialsx.net/releases/</url>
</repository>

<dependency>
  <groupId>net.essentialsx</groupId>
  <artifactId>EssentialsX</artifactId>
  <version>2.20.1</version>
  <scope>provided</scope>
</dependency>
```

---

## 📖 Verwendung im Code

### API importieren
```java
import ch.ksrminecraft.RankPointsAPI.PointsAPI;
```

### API instanziieren
```java
Logger logger = getLogger();
PointsAPI api = new PointsAPI(
    "jdbc:mysql://host:port/database",
    "username",
    "password",
    logger,
    true,   // Debug-Modus
    true    // Staff ausgeschlossen (kein Punktesammeln für Staff)
);
```

### Beispiel: Staff **nicht ausschliessen**
```java
PointsAPI api = new PointsAPI(
    "jdbc:mysql://host:port/database",
    "username",
    "password",
    logger,
    false,  // Debug aus
    false   // Staff darf Punkte sammeln
);
```

### Methoden
- `api.addPoints(UUID, int)` → Punkte hinzufügen
- `api.setPoints(UUID, int)` → Punkte setzen (überschreibt)
- `api.getPoints(UUID)` → Punktestand abfragen

---

## ⚙️ EssentialsX-AFK-Bridge

Wenn EssentialsX installiert ist, wird automatisch der AFK-Status an das Proxy-Plugin gesendet.  
Dies geschieht über den Channel `rankproxy:afk`.

```java
@EventHandler
public void onAfkChange(AfkStatusChangeEvent event) {
    Player player = event.getAffected().getBase();
    boolean isAfk = event.getValue();

    String msg = player.getUniqueId() + ";" + isAfk;
    player.sendPluginMessage(this, "rankproxy:afk", msg.getBytes(StandardCharsets.UTF_8));
}
```

Damit können Proxy-Plugins (wie RankProxyPlugin) erkennen, dass ein Spieler AFK ist.

---

## 🛠️ Datenbankschema

```sql
CREATE TABLE IF NOT EXISTS points (
  UUID VARCHAR(36) PRIMARY KEY,
  points INT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS stafflist (
  UUID VARCHAR(36) PRIMARY KEY,
  name VARCHAR(50) NOT NULL
);
```

---

## 🧩 AFK-Integration

| Komponente | Aufgabe |
|-------------|----------|
| **EssentialsX (Paper)** | Erkennt AFK-Spieler automatisch |
| **RankPointsAPI** | Sendet AFK-Status über `rankproxy:afk` |
| **RankProxyPlugin** | Empfängt Status und stoppt Punktevergabe |
| **SchedulerManager** | Prüft AFK-Status, bevor Punkte vergeben werden |

---

## 🧑‍💻 Beispiel

```java
UUID playerUUID = UUID.fromString("xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx");
api.addPoints(playerUUID, 5);
int current = api.getPoints(playerUUID);
```

---

## 🔍 Erklärung: Was bedeutet „shaded“?

**Shading** = externe Libraries (z. B. MySQL-Treiber) werden in dein JAR eingebettet.  
Das Plugin funktioniert dann autark, benötigt aber Paket-Relocation, um Versionskonflikte zu vermeiden.

Wenn du nicht shadest, muss der MySQL-Treiber auf dem Server vorhanden sein.  
Wenn du shadest, achte darauf, dass das Shade-Plugin die `META-INF/services` korrekt merged.

---

## 🧾 Changelog

| Version    | Änderungen |
|------------|-------------|
| **v0.0.4** | ✨ EssentialsX-AFK-Bridge hinzugefügt (Paper → Proxy) |
| **v0.0.2** | Stafflist-Feature hinzugefügt |
| **v0.0.1** | Erste Version mit MySQL + HikariCP |

---

## 📜 Lizenz

MIT – frei verwendbar und anpassbar.  
Bitte nenne die Quelle, wenn du das Plugin weiterverwendest.
