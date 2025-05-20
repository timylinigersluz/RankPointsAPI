# RankPointsAPI

`RankPointsAPI` is a lightweight and flexible API to manage player points in a distributed Minecraft server environment using a shared MySQL database.  
It provides simple methods to get, set and add points by UUID.

---

## ✅ Features

- MySQL-based player point tracking
- Auto-creates `points` table if missing
- Safe operations (e.g. `INSERT IGNORE`, `ON DUPLICATE KEY`)
- Minimal external dependencies
- Ready for use in Velocity or Bukkit-like plugins

---

## 💡 Installation (Maven)

### Step 1: Add JitPack Repository
```xml
<repository>
  <id>jitpack.io</id>
  <url>https://jitpack.io</url>
</repository>
```

### Step 2: Add Dependency
```xml
<dependency>
  <groupId>com.github.Samhuwsluz</groupId>
  <artifactId>RankPointsAPI</artifactId>
  <version>v0.0.5</version>
</dependency>
```

---

## 📦 API Usage

### Import the API:
```java
import ch.ksrminecraft.RankPointsAPI.PointsAPI;
```

### Instantiate the API:
```java
PointsAPI api = new PointsAPI("jdbc:mysql://host:port/database", "username", "password");
```

---

## 🧩 API Reference

### ➕ Add Points
```java
api.addPoints(UUID uuid, int delta);
```
Adds `delta` points to a player. Automatically inserts the user if not present.

### ➖ Set Points
```java
api.setPoints(UUID uuid, int points);
```
Sets the total points for the user, overwriting any previous value.

### 📊 Get Points
```java
int points = api.getPoints(UUID uuid);
```
Returns the current number of points for a player. Auto-inserts if necessary.

---

## 🛠️ MySQL Table Schema

The plugin will automatically create the required table if it does not exist.

```sql
CREATE TABLE IF NOT EXISTS points (
    UUID VARCHAR(36) PRIMARY KEY,
    points INT NOT NULL DEFAULT 0
);
```

> ✅ Optional: Add timestamp column if desired (not used by API directly):
```sql
ALTER TABLE points ADD COLUMN time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;
```

---

## 🧪 Example

```java
UUID playerUUID = UUID.fromString("xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx");
api.addPoints(playerUUID, 5);
int current = api.getPoints(playerUUID);
```

---

## 🔐 Notes

- Ensure the MySQL user has `INSERT`, `UPDATE`, `SELECT` rights on the database.
- The shaded driver class used: `ch.ksrminecraft.shaded.mysql.cj.jdbc.Driver`

---

## 📄 License

MIT – Use freely, modify responsibly...
