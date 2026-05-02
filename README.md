# 🌐 Système de Surveillance Distribué

**Course:** Distributed Systems — **Academic Year:** 2025–2026  
A real-time distributed platform for collecting, processing, and visualizing system metrics, managing alerts, and providing a modern MVC interface.

---

## 📋 System Requirements

**Essential software to install before running the project:**

| Software | Version Required | Installation Notes |
|----------|------------------|-------------------|
| **Java** | JDK 17+ | [Download Oracle JDK 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) or OpenJDK 17 |
| **Maven** | 3.8+ | [Download Apache Maven](https://maven.apache.org/download.cgi) or use package manager |
| **Node.js** | 18+ | [Download Node.js](https://nodejs.org/) (includes npm) |
| **npm** | 9+ | Comes with Node.js installation |
| **Git** | Latest | [Download Git](https://git-scm.com/) |

**Verification commands:**
```bash
java -version          # Should show Java 17+
mvn -version           # Should show Maven 3.8+
node --version         # Should show Node.js 18+
npm --version          # Should show npm 9+
```

---

## 🎯 Context & Learning Objectives
This project demonstrates a complete distributed system with real-time monitoring.

**Objectives**
- Implement a distributed architecture (Agent → Server → Client)
- Use multi-threading, TCP/UDP networking, and Java RMI
- Build a modern UI with MVC principles
- Separate backend and frontend responsibilities
- Handle concurrency, persistence, and real-time updates

---

## 🏗️ System Architecture

| Component | Role | Communication |
|----------|------|---------------|
| **Monitoring Agent** | Collects CPU/RAM/Disk metrics | UDP for metrics, TCP for critical alerts |
| **Central Server** | Aggregates data, manages agents, evaluates thresholds | UDP/TCP listeners, RMI services, REST API |
| **Client UI** | Visualizes data and alerts, export, configuration | Web (REST) and/or Desktop (RMI) |

---

## ✅ Key Features

- **Real-time Metrics** (CPU / RAM / Disk)
- **History & Statistics**
- **Configurable Alerts**
- **Filtering & Search**
- **User Management** (Admin / Observer)
- **Export CSV / JSON**

---

## 🧰 Tech Stack

| Layer | Technologies |
|------|--------------|
| **Core** | Java 17, Maven, Threads, `java.net`, `java.rmi` |
| **Server** | Spring Boot, UDP/TCP, RMI, H2 |
| **Client (Web)** | React 18, Vite, TailwindCSS, Axios, Recharts |
| **Tools** | Git, JUnit, Mermaid/PlantUML |

---

## 📂 Project Structure

```
Distributed_Systems_Project-/
├── shared/                          # Shared models, constants, and utilities
│   └── src/main/java/com/monitor/shared/
│       ├── model/                   # MetricData, Alert, User, Role, AgentStatus
│       ├── rmi/                     # RMIMetricsService interface
│       ├── constants/               # NetworkConstants, ThresholdConstants
│       └── utils/                   # Logger, SerializationUtils
│
├── agent/                           # Monitoring Agent (UDP sender, TCP alert client)
│   └── src/main/java/com/monitor/agent/
│       ├── core/                    # AgentMain, MonitoringAgent, SystemMetricsCollector
│       ├── network/                 # UDPSender, TCPAlertClient
│       └── threads/                 # MetricPublisherTask (scheduled collection)
│
├── server/                          # Central Server (Spring Boot)
│   └── src/main/java/com/monitor/server/
│       ├── core/                    # ServerMain, ConcurrentDataStore
│       ├── network/                 # UDPServer, TCPServer
│       ├── alerting/                # ThresholdEngine, AlertDispatcher
│       ├── rmi/                     # RMIServer, RMIMetricsServiceImpl
│       ├── rest/                    # MetricsController, AdminController
│       ├── storage/                 # MetricsRepository (H2), MetricsExporter (CSV/JSON)
│       ├── security/                # AuthService
│       └── config/                  # SecurityConfig, ServerInitializer
│
├── client-desktop/                  # Desktop Client (Swing + RMI + REST)
│   └── src/main/java/com/monitor/ui/desktop/
│       ├── main/                    # DesktopApp entry point
│       ├── controller/              # DashboardController
│       ├── view/                    # DashboardView (Swing tabs)
│       └── rmi/                     # RMIServiceProxy
│
├── client-web/                      # Web Client (React + Vite + Tailwind)
│   ├── src/
│   │   ├── main.jsx                 # React entry point
│   │   ├── App.jsx                  # Tabbed dashboard (Metrics / History / Alerts)
│   │   ├── api/api.js               # Axios REST calls
│   │   └── components/
│   │       ├── MetricsTable.jsx     # Live metrics with color-coded badges
│   │       ├── AlertsTable.jsx      # Alerts with severity coloring
│   │       └── AgentChart.jsx       # Recharts line chart for history
│   └── index.html
│
├── docs/                            # Architecture docs & UML diagrams
│   ├── architecture.md
│   ├── guide-utilisation.md
│   └── uml/                         # PlantUML diagram images
│
├── scripts/                         # Helper shell scripts
│   ├── build-all.sh
│   ├── start-server.sh
│   ├── start-agent.sh
│   └── run-tests.sh
│
└── pom.xml                          # Maven multi-module root
```

---

## ▶️ Complete Setup & Run Instructions

### 🚀 Step 1: Clone and Setup
```powershell
# Navigate to Downloads folder
cd Downloads

# Clone the project with a specific folder name
git clone https://github.com/OualidDR/Distributed_Systems_Project- Distributed_Systems_Working_test

# Enter the project folder
cd Distributed_Systems_Working_test
```

### 🔧 Step 2: Build Dependencies
```powershell
# Build shared module first
mvn --% -f shared\pom.xml clean install

# Build agent with dependencies
mvn --% -f agent\pom.xml clean package

# Build server
mvn --% -f server\pom.xml clean package
```

### 📦 Step 3: Install Web Dependencies
```powershell
cd client-web
npm install
cd ..
```

### 🎮 Step 4: Run All Components

**Open 3 separate terminals:**

#### Terminal 1 - Start Server
```bash
mvn --% -f server\pom.xml spring-boot:run -Dspring-boot.run.mainClass=com.monitor.server.core.ServerMain
```

#### Terminal 2 - Start Agent
```bash
java -jar agent\target\agent-1.0-SNAPSHOT.jar
```

#### Terminal 3 - Start Web UI
```bash
cd client-web
npm run dev
```

---

## 🌐 Access Points

| Service | URL | Description |
|---------|-----|-------------|
| **Web UI** | http://localhost:3000 | Main monitoring interface |
| **API Server** | http://localhost:8080/api | REST API endpoints |
| **WebSocket** | ws://localhost:8080/ws | Real-time updates |

---

## 🔧 Troubleshooting

### Common Issues & Solutions

**❌ "Error: Unable to access jarfile agent/target/agent-1.0-SNAPSHOT.jar"**
```bash
# Solution: Build the agent first
mvn --% -f shared\pom.xml clean install
mvn --% -f agent\pom.xml clean package
```

**❌ "Cannot find module 'autoprefixer'"**
```bash
# Solution: Install missing dependency
cd client-web
npm install autoprefixer
```

**❌ "NoClassDefFoundError: com/monitor/shared/utils/Logger"**
```bash
# Solution: Build shared module first
mvn --% -f shared\pom.xml clean install
```

**❌ Port conflicts**
- Change ports in configuration files if needed
- Default: Server (8080), Web UI (3000)

### Quick Setup Script
For automated setup, run:
```bash
scripts\setup-dev.bat
```

---

## 📸 Screenshots

![Dashboard](docs/screenshots/dashboard1.png)  
![Dashboard](docs/screenshots/dashboard2.png)

---
