# Lernassistent - LLM API Modul

Dieses Modul ist ein Bestandteil des [Lernassistenten](https://github.com/Magn4/Lernassistent). Es stellt eine API bereit, um mit verschiedenen LLM-Modellen zu interagieren, sowohl lokal als auch über externe Anbieter.

## Features

- **REST-API Server** zur Kommunikation mit LLMs
- **Unterstützung für lokale Modelle** über Ollama
- **Unterstützung für externe Modelle** über Groq API
- **Modellverwaltung**: Laden, Entladen und Auflisten von Modellen
- **Nicht-streaming Generierung von Antworten**

## Verwendete Technologien

- **Java 17**
- **HttpServer (Java SE)** für die API-Schnittstelle
- **JSON (org.json)** für Datenverarbeitung
- **Docker** (optional für Deployment)

## API-Endpunkte

| Methode | Endpunkt                            | Beschreibung                                       |
| ------- | ----------------------------------- | -------------------------------------------------- |
| `GET`   | `/api/ping`                         | Prüft, ob der Server läuft                         |
| `POST`  | `/api/generateResponseNonStreaming` | Generiert eine Antwort basierend auf einer Eingabe |
| `GET`   | `/api/listModels`                   | Gibt eine Liste aller verfügbaren Modelle zurück   |
| `GET`   | `/api/listRunningModels`            | Listet aktuell laufende Modelle auf                |
| `POST`  | `/api/loadModel`                    | Lädt ein spezifisches Modell                       |

## Einrichtung & Nutzung

### 1. Abhängigkeiten installieren

Stelle sicher, dass du **Java 17+** installiert hast.

### 2. Projekt kompilieren & starten

```sh
javac -d out -cp . $(find . -name "*.java")
java -cp out Main
```

### 3. API testen

Nach dem Start des Servers kann die API z. B. mit `curl` oder Postman getestet werden:

```sh
curl -X GET http://localhost:9191/api/ping
```

### 4. Konfiguration der API-Keys

Der API-Key für externe Modelle mussen in der Datei `groqAPIKey.ini` hinterlegt werden. Diese Datei muss sich im selben Verzeichnis wie die `.jar`-Datei befinden und wie folgt aufgebaut sein:

```
[API]
key=SecretKEY...
```

