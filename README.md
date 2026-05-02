# Distributed Monitoring System

Real-time distributed platform for collecting and visualizing system metrics.

---

## Requirements

| Software | Version |
|----------|---------|
| Java | JDK 17+ |
| Maven | 3.8+ |
| Node.js | 18+ |
| npm | 9+ |

---

## Architecture

| Component | Role |
|----------|------|
| Agent | Collects CPU/RAM/Disk metrics |
| Server | Aggregates data, manages agents |
| Client | Web interface for visualization |

---

## Features

- Real-time metrics (CPU/RAM/Disk)
- History & statistics
- Configurable alerts
- Export CSV/JSON

---

## Tech Stack

| Layer | Technologies |
|------|--------------|
| Core | Java 17, Maven, Threads |
| Server | Spring Boot, UDP/TCP, RMI |
| Client | React 18, Vite, TailwindCSS |

---

## Structure

```
Distributed_Systems_Project-/
├── shared/          # Shared models
├── agent/           # Monitoring agent
├── server/          # Central server
├── client-web/      # Web interface
├── client-desktop/  # Desktop client
├── scripts/         # Helper scripts
└── pom.xml          # Maven root
```

---

## Setup

### Build
```bash
mvn --% -f shared\pom.xml clean install
mvn --% -f agent\pom.xml clean package
mvn --% -f server\pom.xml clean package
cd client-web && npm install
```

### Run
Open 3 terminals:

1. Server: `mvn --% -f server\pom.xml spring-boot:run`
2. Agent: `java -jar agent\target\agent-1.0-SNAPSHOT.jar`
3. Web: `cd client-web && npm run dev`

---

## Access

| Service | URL |
|---------|-----|
| Web UI | http://localhost:3000 |
| API | http://localhost:8080/api |

---

## Issues

**Jar file not found**
```bash
mvn --% -f shared\pom.xml clean install
mvn --% -f agent\pom.xml clean package
```

**Missing module**
```bash
cd client-web && npm install autoprefixer
```

**Port conflicts**
- Default: Server (8080), Web UI (3000)

**Quick setup**
```bash
scripts\setup-dev.bat
```

---
