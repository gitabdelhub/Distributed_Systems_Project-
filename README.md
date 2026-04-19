# 🌐 Distributed Monitoring System

> **Course:** Distributed Systems | **Academic Year:** 2025-2026  
> A real-time distributed platform for collecting, processing, and visualizing system metrics, managing alerts, and providing a modern MVC interface.

---

##  Context & Pedagogical Objectives
Monitoring distributed systems requires tools capable of collecting, processing, and visualizing real-time data from multiple remote machines. This project aims to design a distributed platform that demonstrates core concepts in distributed computing while delivering a functional monitoring solution.

**Key Learning Objectives:**
- ✅ Implement a complete distributed architecture
- ✅ Apply multi-threading, TCP/UDP networking, and Java RMI
- ✅ Design a modern graphical interface following the MVC pattern
- ✅ Understand Backend/Frontend separation and Client/Server principles
- ✅ Manage concurrency, data persistence, and real-time communication

---

## 🏗️ System Architecture
The system follows a **three-tier distributed architecture**:

| Component | Role | Communication |
|-----------|------|---------------|
| **🖥️ Monitoring Agent** | Collects CPU, RAM, and Disk metrics periodically on target machines | Sends metrics via **UDP**, critical alerts via **TCP** |
| **🌐 Central Server** | Aggregates data, manages agents, evaluates thresholds, exposes services | Listens to UDP/TCP, provides **RMI** & **REST** endpoints, stores metrics |
| **📱 Client Interface** | Visualizes data, configures thresholds, manages users & exports | Desktop: **RMI** (Swing/JavaFX) \| Web: **REST** (Angular/React/Vue) |

---

## 📋 Key Features
| Feature | Description |
|---------|-------------|
| 📈 **History & Statistics** | Store and analyze metrics over time to identify trends, peaks, and performance patterns |
| 🚨 **Configurable Alerts** | Define custom thresholds that automatically trigger critical notifications |
|  **Filtering & Search** | Efficiently manage large agent fleets with dynamic sorting, filtering, and search |
| 👥 **User Management** | Secure access with authentication, role-based permissions (Admin/Observer) |
| 📤 **Data Export** | Extract metrics and reports in CSV/JSON formats for external analysis |

---

## ️ Technology Stack
| Layer | Technologies |
|-------|--------------|
| **Core** | Java 17+, Maven Multi-Module, Threads, `java.net`, `java.rmi` |
| **Server** | UDP/TCP Listeners, RMI Registry, Spring Boot/JAX-RS (REST), H2/PostgreSQL |
| **UI Option 1** | JavaFX or Swing, RMI Client, MVC Pattern |
| **UI Option 2** | Angular / React / Vue, TypeScript, REST API, WebSocket (optional) |
| **Tools** | Git, PlantUML/Mermaid, Docker (optional), JUnit |

---

## 📂 Project Structure


