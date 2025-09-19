# RankPointsAPI

**RankPointsAPI** ist eine leichtgewichtige und flexible Java-API, um Spielerpunkte in einem verteilten Minecraft-Servernetzwerk (Velocity + Paper-Server) zentral in einer **MySQL-Datenbank** zu verwalten.  
Die API stellt einfache Methoden bereit, um Punkte anhand der Spieler-UUID zu **lesen**, **setzen** und **hinzuzufügen**.  
Zusätzlich gibt es einen **konfigurierbaren Staff-Ausschluss**: Entwickler können selbst festlegen, ob Staff-Mitglieder Punkte sammeln dürfen oder nicht.

---

## 💡 Anwendungsfall (Use Case)

- Alle Punkte (z. B. aus SMP, Minigames oder Proxy-Spielzeit) werden **global synchronisiert**.
- Typische Punktequellen:
    - SMP: Blockabbau/-platzierung, Advancements, Endboss-Kills
    - Proxy: Spielzeit (z. B. 1 Punkt pro Minute)
    - Minigames: Siege, Platzierungen, Rekorde
- Staff-Mitglieder (Owner, Admins, Mods) stehen in einer separaten `stafflist`.

**Neu:** Du kannst beim Erstellen der API entscheiden, ob Staff **Punkte bekommt oder nicht**.

---

## ✅ Features

- MySQL-basierte Speicherung (Tabelle `points`)
- Automatische Erstellung von Tabellen (`points` und `stafflist`)
- Staff-Ausschluss optional (`excludeStaff = true/false`)
- Sichere SQL-Abfragen (`PreparedStatement`, `ON DUPLICATE KEY`)
- Kompatibel mit Velocity- und Bukkit/Paper-Plugins
- Stabile MySQL-Verbindungen durch HikariCP-Pooling

---

## 📦 Installation in dein Plugin

### Schritt 1: JitPack-Repository hinzufügen
```xml
<repository>
  <id>jitpack.io</id>
  <url>https://jitpack.io</url>
</repository>
```

### Schritt 2: Dependency einbinden (vX.X.X durch die aktuellste Version ersetzen)
```xml
<dependency>
  <groupId>com.github.timylinigersluz</groupId>
  <artifactId>RankPointsAPI</artifactId>
  <version>vX.X.X</version> //hier die Version ersetzen!
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

- Steht eine UUID in `stafflist`, wird sie **nur berücksichtigt**, wenn `excludeStaff = true`.
- Mit `excludeStaff = false` verhält sich Staff wie normale Spieler.

---

## 🧑‍💻 Beispiel

```java
UUID playerUUID = UUID.fromString("xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx");
api.addPoints(playerUUID, 5);
int current = api.getPoints(playerUUID);
```

---

## 🔍 Erklärung: Was bedeutet „shaded“?

- **Shading** bedeutet, dass du externe Bibliotheken (z. B. MySQL-Treiber) **direkt in dein Plugin-JAR einpackst**.
- Vorteil: Dein Plugin funktioniert unabhängig.
- Nachteil: Die JAR wird größer, und es braucht Relocation, um Versionskonflikte zu vermeiden.

Wenn du **nicht shadest**, muss der MySQL-Treiber als **externe Abhängigkeit** auf dem Server verfügbar sein.  
Wenn du **shadest**, musst du im Build-Tool (z. B. Maven Shade Plugin) darauf achten, die Pakete umzubenennen und SPI-Dateien korrekt zusammenzuführen.

---

## 📜 Lizenz

MIT – frei verwendbar und anpassbar.  
Bitte nenne die Quelle, wenn du das Plugin weiterverwendest.
