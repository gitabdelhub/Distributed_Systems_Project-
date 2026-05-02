# 🌐 Système de Surveillance

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
├─ server/              # Spring Boot server (REST + RMI + UDP/TCP)
├─ agent/               # Monitoring agent
├─ shared/              # Shared utilities and models
├─ client-web/          # Web UI (React + Vite)
│  ├─ src/
│  │  ├─ components/    # React components
│  │  ├─ App.jsx        # Main application
│  │  └─ main.jsx       # Entry point
│  ├─ package.json      # Node.js dependencies
│  └─ vite.config.js    # Vite configuration
├─ scripts/             # Start scripts
└─ docs/                # Documentation
```

---

## 🆕 New Files Added (Web UI)

The following files were created to build the modern React interface:

### Core React Components
- `client-web/src/App.jsx` - Main application component with state management
- `client-web/src/main.jsx` - React application entry point
- `client-web/index.html` - HTML entry point

### UI Components
- `client-web/src/components/Header.jsx` - Blue header with title and export buttons
- `client-web/src/components/NavigationTabs.jsx` - Tab navigation with alert badge
- `client-web/src/components/MetricsTable.jsx` - Real-time metrics table with conditional formatting
- `client-web/src/components/AlertsSection.jsx` - Alerts display section
- `client-web/src/components/Footer.jsx` - Footer with copyright

### Configuration Files
- `client-web/vite.config.js` - Vite build configuration
- `client-web/tailwind.config.js` - TailwindCSS configuration
- `client-web/package.json` - Node.js dependencies (React, Vite, TailwindCSS, Axios, Recharts)

### Styling Files
- `client-web/src/index.css` - Global styles with TailwindCSS
- `client-web/src/App.css` - Application-specific styles
- Individual CSS files for each component (Header.css, NavigationTabs.css, etc.)

---

## ▶️ Complete Setup & Run Instructions

### 🚀 Step 1: Clone and Setup
```bash
git clone https://github.com/OualidDR/Distributed_Systems_Project-
cd Distributed_Systems_Project-
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

#### Terminal 1 - PowerShell: Start Server
```powershell
cd C:\Users\user\Downloads\Distributed_Systems_Project-
mvn --% -f server\pom.xml spring-boot:run -Dspring-boot.run.mainClass=com.monitor.server.core.ServerMain -Dspring-boot.run.arguments="--spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration"
```

#### Terminal 2 - Git Bash: Start Agent
```bash
cd /c/Users/user/Downloads/Distributed_Systems_Project-
./scripts/start-agent.sh
```

#### Terminal 3 - PowerShell: Start Web UI
```powershell
cd C:\Users\user\Downloads\Distributed_Systems_Project-\client-web
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
```powershell
# Solution: Build the agent first
mvn --% -f shared\pom.xml clean install
mvn --% -f agent\pom.xml clean package
```

**❌ "Cannot find module 'autoprefixer'"**
```powershell
# Solution: Install missing dependency
cd client-web
npm install autoprefixer
```

**❌ "NoClassDefFoundError: com/monitor/shared/utils/Logger"**
```powershell
# Solution: Build shared module first
mvn --% -f shared\pom.xml clean install
```

**❌ Port conflicts**
- Change ports in configuration files if needed
- Default: Server (8080), Web UI (3000)

---

## 📸 Screenshots

![Dashboard](docs/screenshots/dashboard1.png)  
![Dashboard](docs/screenshots/dashboard2.png)

---
