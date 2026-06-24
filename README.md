# RankPointsAPI

**RankPointsAPI** ist eine leichtgewichtige und flexible Java-API, um Spielerpunkte in einem verteilten Minecraft-Servernetzwerk zentral in einer **MySQL- oder MariaDB-Datenbank** zu verwalten.

Die API stellt einfache Methoden bereit, um Punkte anhand der Spieler-UUID zu **lesen**, **setzen** und **hinzuzufügen**. Zusätzlich gibt es einen **konfigurierbaren Staff-Ausschluss**: Entwickler können festlegen, ob Staff-Mitglieder Punkte sammeln dürfen oder nicht.

Die API ist für den Einsatz in einem Minecraft-Servercluster mit **Velocity + Paper-Servern** gedacht. Punkte aus SMP, Minigames und Proxy-Spielzeit können damit global synchronisiert werden.

Ab **v0.0.6** ist die Datenbankbehandlung deutlich robuster: HikariCP wird defensiv konfiguriert, kurzzeitig nicht erreichbare Datenbanken führen nicht mehr direkt zum Absturz, und wiederholte Datenbankfehler werden gedrosselt geloggt.

---

## 💡 Anwendungsfall

Alle Rangpunkte werden zentral gespeichert und können von verschiedenen Plugins geschrieben oder gelesen werden.

Typische Punktequellen:

* **SMP:** Blockabbau, Blockplatzierung, Advancements, Endboss-Kills
* **Proxy:** Spielzeit, z. B. 1 Punkt pro Minute
* **Minigames:** Siege, Platzierungen, Rekorde
* **Admin-/Staff-Befehle:** Manuelles Setzen oder Hinzufügen von Punkten

Staff-Mitglieder können optional vom Punktesammeln ausgeschlossen werden. Dafür wird eine zentrale `stafflist`-Tabelle verwendet.

Zusätzlich kann der AFK-Status von Paper-Servern über einen Plugin-Channel an das Proxy-Plugin übertragen werden. So kann das Proxy-Plugin verhindern, dass AFK-Spieler weiterhin Spielzeitpunkte erhalten.

---

## ✅ Features

* MySQL-/MariaDB-basierte Speicherung
* Tabellen `points` und `stafflist`
* Automatische Tabellenerstellung
* Punkte lesen, setzen und hinzufügen
* Staff-Ausschluss optional (`excludeStaff = true/false`)
* Sichere SQL-Abfragen mit `PreparedStatement`
* Sichere Updates mit `ON DUPLICATE KEY UPDATE`
* Kompatibel mit Velocity, Bukkit und Paper
* HikariCP-Connection-Pooling
* Robuste Pool-Konfiguration für Servercluster
* Kein dauerhaftes Halten einzelner Datenbankverbindungen
* Datenbankfehler werden gedrosselt geloggt
* Kurzzeitig nicht erreichbare Datenbank blockiert den Pluginstart nicht mehr
* Optional nutzbar mit EssentialsX-AFK-Bridge über Plugin-Channel

---

## 🧱 Robuste Datenbankbehandlung ab v0.0.6

Ab Version **v0.0.6** ist die HikariCP-Konfiguration bewusst defensiv gewählt, damit die API in einem Minecraft-Servercluster stabiler läuft.

Die Standardwerte des Pools sind:

```java
maximumPoolSize = 3
minimumIdle = 0
maxLifetime = 120000
connectionTimeout = 5000
validationTimeout = 3000
initializationFailTimeout = -1
```

### Bedeutung

| Einstellung                      | Wirkung                                                                     |
| -------------------------------- | --------------------------------------------------------------------------- |
| `maximumPoolSize = 3`            | Begrenzte Anzahl paralleler DB-Verbindungen pro Plugininstanz               |
| `minimumIdle = 0`                | Keine unnötig offen gehaltenen Idle-Verbindungen                            |
| `maxLifetime = 120000`           | Verbindungen werden früh erneuert, bevor MariaDB sie serverseitig schliesst |
| `connectionTimeout = 5000`       | Maximal 5 Sekunden warten, bis eine Verbindung verfügbar ist                |
| `validationTimeout = 3000`       | Maximal 3 Sekunden für Connection-Validierung                               |
| `initializationFailTimeout = -1` | Pool darf starten, auch wenn die DB gerade nicht erreichbar ist             |

Dadurch werden typische Fehler reduziert, zum Beispiel:

```text
Failed to validate connection
No operations allowed after connection closed
Connection.setNetworkTimeout cannot be called on a closed connection
Connection is not available
```

Falls die Datenbank beim Start kurz nicht erreichbar ist, wird das Plugin nicht sofort beendet. Die API versucht beim nächsten Datenbankzugriff erneut, eine Verbindung aufzubauen und das Schema zu prüfen.

---

## 📦 Installation in dein Plugin

### Schritt 1: JitPack-Repository hinzufügen

```xml
<repository>
  <id>jitpack.io</id>
  <url>https://jitpack.io</url>
</repository>
```

### Schritt 2: Dependency einbinden

```xml
<dependency>
  <groupId>com.github.timylinigersluz</groupId>
  <artifactId>RankPointsAPI</artifactId>
  <version>v0.0.6</version>
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
    true    // Staff ausgeschlossen: Staff sammelt keine Punkte
);
```

### Beispiel: Staff nicht ausschliessen

```java
Logger logger = getLogger();

PointsAPI api = new PointsAPI(
    "jdbc:mysql://host:port/database",
    "username",
    "password",
    logger,
    false,  // Debug aus
    false   // Staff darf Punkte sammeln
);
```

---

## 🧩 API-Methoden

### Punkte hinzufügen

```java
api.addPoints(uuid, 5);
```

Fügt dem Spieler Punkte hinzu.

Wenn `excludeStaff = true` gesetzt ist und der Spieler in der `stafflist` steht, werden keine Punkte hinzugefügt.

---

### Punkte setzen

```java
api.setPoints(uuid, 100);
```

Setzt den Punktestand eines Spielers auf einen festen Wert.

Wenn `excludeStaff = true` gesetzt ist und der Spieler in der `stafflist` steht, wird der Wert nicht verändert.

---

### Punkte abfragen

```java
int points = api.getPoints(uuid);
```

Liest den aktuellen Punktestand eines Spielers.

Falls der Spieler noch keinen Eintrag in der Datenbank hat, wird `0` zurückgegeben.

---

### API sauber schliessen

```java
api.close();
```

Schliesst den HikariCP-Pool sauber. Diese Methode sollte beim Deaktivieren des Plugins aufgerufen werden.

Beispiel in einem Paper-Plugin:

```java
@Override
public void onDisable() {
    if (pointsAPI != null) {
        pointsAPI.close();
    }
}
```

---

## 🛠️ Datenbankschema

Die benötigten Tabellen werden automatisch erstellt, falls sie noch nicht existieren.

```sql
CREATE TABLE IF NOT EXISTS points (
  UUID VARCHAR(36) PRIMARY KEY,
  points INT NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS stafflist (
  UUID VARCHAR(36) PRIMARY KEY,
  name VARCHAR(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

---

## 🧑‍💻 Beispiel

```java
UUID playerUUID = UUID.fromString("xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx");

api.addPoints(playerUUID, 5);

int current = api.getPoints(playerUUID);

System.out.println("Aktuelle Punkte: " + current);
```

---

## ⚙️ Optional: EssentialsX-AFK-Bridge

Die RankPointsAPI selbst speichert keine AFK-Daten. Der AFK-Status kann aber von einem Paper-Plugin über einen Plugin-Channel an das Proxy-Plugin gesendet werden.

Wenn EssentialsX installiert ist, kann der AFK-Status über den Channel `rankproxy:afk` an das RankProxyPlugin gemeldet werden.

Beispiel:

```java
@EventHandler
public void onAfkChange(AfkStatusChangeEvent event) {
    Player player = event.getAffected().getBase();
    boolean isAfk = event.getValue();

    String msg = player.getUniqueId() + ";" + isAfk;
    player.sendPluginMessage(this, "rankproxy:afk", msg.getBytes(StandardCharsets.UTF_8));
}
```

Damit kann das RankProxyPlugin erkennen, dass ein Spieler AFK ist und die Punktevergabe für Spielzeit pausieren.

---

## 🧩 AFK-Integration im Gesamtsystem

| Komponente                    | Aufgabe                                                 |
| ----------------------------- | ------------------------------------------------------- |
| **EssentialsX auf Paper**     | Erkennt automatisch, ob ein Spieler AFK ist             |
| **Paper-Plugin / AFK-Bridge** | Sendet den AFK-Status über `rankproxy:afk`              |
| **RankProxyPlugin**           | Empfängt den AFK-Status                                 |
| **SchedulerManager**          | Prüft AFK-Status, bevor Spielzeitpunkte vergeben werden |
| **RankPointsAPI**             | Schreibt oder liest Punkte in der zentralen Datenbank   |

---

## 🔍 Erklärung: Was bedeutet „shaded“?

**Shading** bedeutet, dass externe Libraries wie HikariCP oder der MySQL-Treiber direkt in das fertige JAR eingebettet werden.

Vorteile:

* Das Plugin ist unabhängiger von extern installierten Libraries.
* Der Server muss den MySQL-Treiber nicht separat bereitstellen.
* Unterschiedliche Plugin-Versionen können Konflikte vermeiden, wenn sauber relocated wird.

Wichtig:

Wenn Libraries geshaded werden, sollte das Maven Shade Plugin auch die `META-INF/services` korrekt zusammenführen. Dafür wird normalerweise der `ServicesResourceTransformer` verwendet.

---

## 🔐 Hinweise für den produktiven Einsatz

* Verwende für alle Plugins im Cluster dieselbe Datenbank.
* Gib dem MySQL-/MariaDB-Benutzer nur die nötigen Rechte.
* Empfohlene Rechte:

  * `SELECT`
  * `INSERT`
  * `UPDATE`
  * `CREATE`
* Rufe `api.close()` beim Plugin-Shutdown auf.
* Verwende keine dauerhaft gehaltenen `Connection`-Objekte ausserhalb der API.
* Bei temporären Datenbankproblemen schreibt die API Warnungen gedrosselt in die Konsole.

---

## 🧾 Changelog

| Version    | Änderungen                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
| ---------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **v0.0.6** | Robuste HikariCP-Konfiguration hinzugefügt; `maximumPoolSize=3`, `minimumIdle=0`, `maxLifetime=120000`, `connectionTimeout=5000`, `validationTimeout=3000`, `initializationFailTimeout=-1`; Plugin/API startet weiter, wenn die DB kurzfristig nicht erreichbar ist; Schema-Prüfung wird beim nächsten Zugriff erneut versucht; DB-Fehler werden über `DbErrorThrottle` höchstens einmal alle 5 Minuten pro identischem Fehler geloggt; keine dauerhaft gehaltenen Connections; `try-with-resources` konsequent beibehalten |
| **v0.0.4** | EssentialsX-AFK-Bridge hinzugefügt (Paper → Proxy über `rankproxy:afk`)                                                                                                                                                                                                                                                                                                                                                                                                                                                     |
| **v0.0.2** | Stafflist-Feature hinzugefügt                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               |
| **v0.0.1** | Erste Version mit MySQL + HikariCP                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          |

---

## 📜 Lizenz

MIT – frei verwendbar und anpassbar.

Bitte nenne die Quelle, wenn du das Plugin weiterverwendest.
