# 🌐 Distributed Monitoring System

> **Cours :** Systèmes Distribués | **Année :** 2025-2026  
> Plateforme distribuée de surveillance en temps réel — collecte, traitement et visualisation des métriques système.

---

## 🏗️ Architecture du système

```
┌─────────────┐  UDP:5000   ┌──────────────────┐   RMI:1099   ┌───────────────────┐
│    AGENT    │ ──────────► │                  │ ◄──────────► │  Client Desktop   │
│   (Java)    │  TCP:6000   │ SERVEUR CENTRAL  │              │  (Swing / Java)   │
└─────────────┘ ──────────► │  Spring Boot     │   REST:8080  └───────────────────┘
                             │  :8080           │ ◄──────────► ┌───────────────────┐
                             └──────────────────┘              │   Client Web      │
                                                               │  (React / Vite)   │
                                                               └───────────────────┘
```

| Composant | Rôle | Communication |
|-----------|------|---------------|
| **Agent** | Collecte CPU/RAM/Disk toutes les 5 s (OSHI) | Envoie via **UDP** (métriques) + **TCP** (alertes critiques) |
| **Serveur** | Agrège les données, évalue les seuils, expose les services | **UDP** + **TCP** listeners, **RMI** registry, **REST** API |
| **Client Desktop** | Affichage temps réel avec onglets et code couleur | **RMI** (Java Swing) |
| **Client Web** | Dashboard interactif, graphiques, export | **REST** (React + Recharts) |

---

## 📂 Structure du projet

```
Distributed_Systems_Project-/
├── shared/                        # Module partagé (modèles, constantes, interface RMI)
│   └── src/main/java/com/monitor/shared/
│       ├── constants/             # NetworkConstants, ThresholdConstants, Constants
│       ├── model/                 # MetricData, Alert, AgentStatus, User, Role
│       ├── rmi/                   # RMIMetricsService (interface partagée)
│       └── utils/                 # Logger, SerializationUtils
│
├── agent/                         # Agent de surveillance (JVM autonome)
│   └── src/main/java/com/monitor/agent/
│       ├── config/                # AgentConfig (lecture agent.properties)
│       ├── core/                  # AgentMain, MonitoringAgent, SystemMetricsCollector
│       ├── network/               # UDPSender, TCPAlertClient
│       └── threads/               # MetricPublisherTask
│
├── server/                        # Serveur central (Spring Boot)
│   └── src/main/java/com/monitor/server/
│       ├── alerting/              # ThresholdEngine, AlertDispatcher
│       ├── config/                # SecurityConfig, ServerInitializer
│       ├── core/                  # ServerMain, ConcurrentDataStore
│       ├── network/               # UDPServer (port 5000), TCPServer (port 6000)
│       ├── rest/                  # MetricsController (/api/*)
│       ├── rmi/                   # RMIMetricsServiceImpl
│       ├── security/              # AuthService (admin/viewer)
│       └── storage/               # MetricsExporter (CSV/JSON)
│
├── client-desktop/                # Client Swing via RMI
│   └── src/main/java/com/monitor/ui/desktop/
│       ├── controller/            # DashboardController (MVC)
│       ├── main/                  # DesktopApp
│       ├── rmi/                   # RMIServiceProxy
│       └── view/                  # DashboardView (onglets, code couleur)
│
├── client-web/                    # Client React (SPA)
│   ├── src/
│   │   ├── App.jsx                # Composant principal (métriques, alertes, export)
│   │   ├── main.jsx               # Point d'entrée React
│   │   └── index.css              # Styles (sans framework CSS)
│   ├── index.html
│   ├── vite.config.js             # Proxy /api → localhost:8080
│   └── package.json
│
├── docs/
│   ├── architecture.md            # Document d'architecture 15-20 pages
│   ├── guide-utilisation.md       # Guide utilisateur complet
│   ├── presentation/              # Script & plan de présentation 10 min
│   └── uml/                       # Diagrammes UML (use case, classe, séquence)
│
├── scripts/
│   ├── build-all.sh               # mvn clean install -DskipTests
│   ├── start-server.sh            # Lance le serveur Spring Boot
│   ├── start-agent.sh             # Lance un agent de surveillance
│   ├── start-web.sh               # Lance le client web React (npm run dev)
│   └── run-tests.sh               # mvn test
│
└── pom.xml                        # POM parent multi-module
```

---

## 🚀 Démarrage rapide

### Prérequis

| Outil | Version |
|-------|---------|
| JDK | **17+** |
| Maven | **3.8+** |
| Node.js | **18+** |

### 1. Build

```bash
bash scripts/build-all.sh
# Ou : mvn clean install -DskipTests
```

### 2. Démarrer le serveur

```bash
bash scripts/start-server.sh
# → UDP :5000  TCP :6000  RMI :1099  REST :8080
```

### 3. Démarrer un agent

```bash
bash scripts/start-agent.sh
# → Métriques envoyées toutes les 5 secondes
```

### 4. Ouvrir les interfaces

```bash
# Client Desktop (Swing / RMI)
java -jar client-desktop/target/client-desktop-1.0-SNAPSHOT.jar

# Client Web (React / REST)
bash scripts/start-web.sh
# → http://localhost:5173
```

---

## 🌐 API REST

Base URL : `http://localhost:8080/api`

| Endpoint | Description |
|----------|-------------|
| `GET /api/agents` | Liste des agents actifs |
| `GET /api/metrics/latest` | Dernières métriques (tous agents) |
| `GET /api/metrics/{id}/history` | Historique d'un agent |
| `GET /api/alerts` | Toutes les alertes |
| `GET /api/export/csv` | Export CSV |
| `GET /api/export/json` | Export JSON |
| `GET /h2-console` | Console H2 (base de données) |

---

## ⚙️ Configuration agent

Fichier : `agent/src/main/resources/agent.properties`

```properties
agent.server.host=localhost     # IP du serveur central
agent.server.port.udp=5000      # Port UDP métriques
agent.server.port.tcp=6000      # Port TCP alertes
agent.send.interval.ms=5000     # Intervalle d'envoi (ms)
```

---

## 🔔 Seuils d'alerte

| Métrique | Seuil | Sévérité |
|----------|-------|----------|
| CPU | > 85 % | CRITICAL |
| RAM | > 90 % | CRITICAL |
| Disque | > 95 % | CRITICAL |

---

## 📚 Documentation

| Document | Contenu |
|----------|---------|
| [`docs/guide-utilisation.md`](docs/guide-utilisation.md) | Guide complet (build, lancement, config, API, dépannage) |
| [`docs/architecture.md`](docs/architecture.md) | Architecture 15-20 pages (UML, flux, protocoles, déploiement) |
| [`docs/presentation/presentation.md`](docs/presentation/presentation.md) | Script de présentation 10 minutes |
| [`docs/uml/`](docs/uml/) | Diagrammes UML (use case, classe, séquence) |

---

## 🛠️ Stack technologique

| Couche | Technologies |
|--------|--------------|
| **Collecte** | Java 17, OSHI 6.4 (CPU/RAM/Disk cross-platform) |
| **Transport** | UDP (métriques), TCP (alertes), Java RMI (desktop), REST/HTTP (web) |
| **Serveur** | Spring Boot 3.2, H2 (in-memory), ConcurrentHashMap |
| **Desktop** | Java Swing, MVC pattern |
| **Web** | React 18, Vite 5, axios, Recharts |
| **Build** | Maven multi-module, npm |

