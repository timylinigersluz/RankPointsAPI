# Installation (Maven):
## Step1:
Add the JitPack repository to your build file:
```xml
<repository>
  <id>jitpack.io</id>
  <url>https://jitpack.io</url>
</repository>
```

## Step2:
Add the dependency
```xml
<dependency>
  <groupId>com.github.Samhuwsluz</groupId>
  <artifactId>RangAPI</artifactId>
  <version>alpha-4</version>
</dependency>
```

# API Usage
## Importing the API
```java
import  ch.ksrminecraft.rangAPI.RangAPI;
```

## Add RangAPI as Bukkit dependency (plugin.yml)
```yaml
softdepend: ['RangAPI']
```

## Loading Plugin instance
```java
 RangAPI api = (RangAPI) this.getServer().getPluginManager().getPlugin("RangAPI");
```


# API Reference

## Get Points from User
```java
public int getPoints(Player p);
```

## Set Points from User
```java
public void setPoints(Player p, int points);
```

# Configuration (Plugin)
## config.yml
The Config File has 3 Fields for connecting to the Points Database: 

```yml
database-url: # URL to the Database ex: mysql://ksrminecraft.ch/[Databasename]
database-user: # Database User
database-password: # Password for the database
```

## DB-Schema
The Database should have a **Table** called **points**
This Table must have the Fields: **UUID:text, points:int(11), time:timestamp**
