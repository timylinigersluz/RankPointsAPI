# Rangsystem

## 0. Kurzbeschrieb des Projekts

### 0.1 Proxy-Plugin

Wir machen ein Minecraft Plugin, welches als Dependency für andere Plugins fungiert. Dieses Plugin stellt direkt eine Verbindung mit der Datenbank her, auf welcher Punkte geschrieben und davon gelesen werden durch ebendieses Plugin. Andere Plugins können dann die Methoden dieses Plugins benutzen, um selber Punkte zu schreiben.

## 1. Ränge

### 1.1 Rangliste

Die KSRMinecraft Ränge werden in der darauffolgenden Liste aufgeführt und sind final. Zu beachten ist insbesondere, dass der Spieler bei Spielstart sich bereits in Besitz des Einsteigerrangs, der Liste entsprechend dem ersten Element, befindet.

1. Einsteigerrang

2. Iron

3. Iron+

4. Bronze

5. Bronze+

6. Silver

7. Silver+

8. Gold

9. Gold+

10. Diamond

11. Diamond+

12. Netherite

### 1.2 Rangvorteile

## 2. Punkte gewinnen

### 2.1 Punkte allgemein

Punkte können auf allen Teilservern mit den folgenden Mitteln verteilt werden:

1. Spielzeit

2. Staff kann Punkte vergeben

### 2.2 Punkte SMP

Die Punkte für den SMP-Server können durch die nachfolgenden Aktionen verdient werden:

1. Achievements

2. Blöcke (ab-)gebaut

3. Endbosse besiegen

4. Blöcke traversiert

5. Evtl. zusätzliche Achievement-Datapacks

### 2.3 Punkte Minigames

Die nachfolgenden Punkte sind Richtideen für die jeweiligen Minigame-Developer.

1. Runden gespielt

2. Top `n%` bekommen Punkte

3. Rekord gibt Punkte

## 3. Regelungen

### 3.1 Allgemeine Regelungen

Diese Regelungen gelten für alle Teilserver und sind zu beachten, vor allem auch der Richtwert der Punkte pro Spielzeit ist als grundlegender Standart für alle Punktevergaben zu beachten!

1. Ein Punkt entspricht einer Minute Spielzeit.

2. Rangprogression ist steigend

3. Der AFK-Timer ist auf 10 Minuten eingestellt.

## 4. Projektstruktur

### 4.1 Plugins

#### 4.1.1 Library

Die Idee ist, eine Library zur Verfügung zu stellen, welche die `PointSetter`-Klasse als Schnittstelle zur Datenbank zur Verfügung stellt.

#### 4.1.2 Proxy-Plugin

Das Proxy-Plugin implementiert die Punktevergabe für die Spielzeit für das gesamte Servercluster. Ebenfalls werden die Staff-Funktionen zum analogen Hinzufügen und Entfernen von Punkten hiermit implementiert.

#### 4.1.3 SMP-Plugin

Das Plugin auf dem SMP soll die Punkte für diverse Aktionen vergeben.

## 5. Zeitplan

### 5.1 Pendenzen

- [x] Projekte erstellen

- [x] Datenbank erstellen

- [ ] API-Plugin MVP
  
  - [ ] Java Library
  
  - [ ] Kann installiert werden von anderen Plugins
  
  - [x] Punkte schreiben auf die Datenbank
  
  - [x] Punkte lesen von der Datenbank
  
  - [x] Config wird erstellt
  
  - [x] Config kann gelesen werden

- [ ] Proxy Plugin MVP
  
  - [ ] API als Dependency
  
  - [ ] Liest Punkte und führt Befehle aus.
  
  - [ ] Läuft auf dem Proxy selber (auf allen Servern)

- [ ] Proxy-Time Plugin
  
  - [ ] Vergibt Punkte für Spielzeit (Alle Server)
  
  - [ ] API als Dependency
  
  - [ ] Läuft auf dem Proxy selber (Auf allen Servern)
  
  - [ ] Adminfunktionen (Alle Server, In Zukunft)

- [ ] SMP Plugin MVP
  
  - [ ] API als Dependency
  
  - [ ] Läuft auf dem SMP
  
  - [ ] Vergibt für SMP-spezifische Aktionen Punkte

### 5.2 Zeitplan

| Deadline | Was                                                            | Wer | Infos                                                              |
| -------- | -------------------------------------------------------------- | --- | ------------------------------------------------------------------ |
| 28.10.24 |                                                                |     |                                                                    |
| 04.11.24 | Informieren was benötigt wird für Installation als Library     | JS  |                                                                    |
| 11.11.24 | Plugin einrichten                                              | S   | Git Rechte an Jean geben (Kollaboration)                           |
| 18.11.24 | API-Plugin kann installiert werden als Dependency              | JS  | PointSetter-Klasse abstrakt definiert und Pluginstruktur vorhanden |
| 25.11.24 |                                                                |     |                                                                    |
| 2.12.24  | Datenbankfunktionen parat und Dummy Test Plugin funktionsfähig | JS  | Kann von einem Dummy-Plugin benutzt werden als Dependency          |
| 9.12.24  | Fertigstellung und Bugfixing                                   | JS  |                                                                    |
| 16.12.24 | Library komplett fertig und getestet                           | JS  |                                                                    |
| 23.12.24 | Ferien (wohlverdient)                                          |     | Proxy-Plugin Brainstorm                                            |
| 30.12.24 | Ferien (wohlverdient)                                          |     | Proxy-Plugin Brainstorm                                            |
| 06.01.25 | Plugin eingerichtet                                            | JS  | Struktur definiert und Plugin erstellt                             |
| 13.01.25 | Proxy Plugin Dependenies funktionieren                         | JS  | Plugin kann auf LPV zugreifen und läuft auf Proxy                  |
| 20.01.25 |                                                                |     |                                                                    |
| 27.01.25 | Punkteauslesesystem fertig                                     | JS  | YAML-Parsing                                                       |
| 03.02.25 | LuckPerms implementiert                                        | JS  |                                                                    |
| 10.02.25 | Plugin ausbessern & fertigstellen                              | JS  |                                                                    |
| 17.02.25 | Buffer                                                         | JS  |                                                                    |
| 21.02.25 | Fertig mit API und Proxy-Plugin                                | JS  | Feierabendbier                                                     |
| 10.03.25 | Time-Plugin eingerichtet                                       | JS  | Plugin erstellt                                                    |
| 17.03.25 | Time-Plugin Brainstorming                                      | JS  | Wie lesen wir Zeit aus?                                            |
| 24.03.25 | Coding-Start                                                   | JS  |                                                                    |
| 31.03.25 | Plugin kann Zeit lesen.                                        | JS  |                                                                    |
| 07.04.25 | Plugin ist fertig                                              | JS  |                                                                    |
| 14.04.25 | Buffer                                                         | JS  |                                                                    |
| 21.04.25 | Buffer                                                         | JS  |                                                                    |
| 25.04.25 | Deadline (ganzes Projekt)                                      | JS  | Feierabendbier und dieses Mal richtig!                             |
|          |                                                                |     |                                                                    |

### 6. Stand der Dinge

#### 6.1 Stand der Dinge (02.12.24)

Datenbank-Verbindung kann aufgebaut werden, Methoden für Punkte lesen und schreiben implementiert. Config-Datei implementiert. Test-Plugin jetzt in Arbeit.
