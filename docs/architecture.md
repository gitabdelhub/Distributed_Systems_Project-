# Architecture du Système de Surveillance Distribué

> **Cours :** Systèmes Distribués | **Année :** 2025-2026  
> Document d'architecture — référence pour le rapport (15–20 pages)

---

## 1. Vue d'ensemble

Le système suit une **architecture distribuée à trois niveaux** :

```
┌──────────────────┐        UDP :5000        ┌──────────────────────────┐
│   Agent (Java)   │ ───────────────────────► │                          │
│  SystemMetrics   │        TCP :6000        │   Serveur Central        │
│  Collector       │ ───────────────────────► │   (Spring Boot :8080)    │
│  (OSHI)          │                         │                          │
│  UUID agentId    │                         │  ┌────────────────────┐  │
└──────────────────┘                         │  │ ConcurrentDataStore│  │
                                             │  │ (in-memory, thread-│  │
┌──────────────────┐        RMI :1099        │  │  safe)             │  │
│ Client Desktop   │ ◄──────────────────────►│  └────────────────────┘  │
│   (Swing MVC)    │                         │                          │
└──────────────────┘      REST :8080         └──────────────────────────┘
                                                          ▲
┌──────────────────┐       GET /api/*                     │
│ Client Web       │ ────────────────────────────────────►│
│  (React + Vite)  │                                      │
└──────────────────┘
```

---

## 2. Modules Maven

Le projet est structuré en **4 modules Maven** + 1 projet npm :

| Module | Rôle | Artefact |
|--------|------|----------|
| `shared` | Modèles, constantes, interface RMI, utilitaires | `shared-1.0-SNAPSHOT.jar` |
| `agent` | Collecte métriques + envoi UDP/TCP | `agent-1.0-SNAPSHOT.jar` |
| `server` | Spring Boot : REST, RMI, UDP/TCP serveurs | `server-1.0-SNAPSHOT.jar` |
| `client-desktop` | UI Swing via RMI | `client-desktop-1.0-SNAPSHOT.jar` |
| `client-web` | React SPA via REST | `npm run dev` |

**Graphe de dépendances :**
```
shared ←── agent
shared ←── server
shared ←── client-desktop
(client-web n'a pas de dépendance Java)
```

---

## 3. Module `shared`

### 3.1 Modèles de données

| Classe | Type | Description |
|--------|------|-------------|
| `MetricData` | `record` | CPU%, RAM%, Disk%, agentId, timestamp |
| `Alert` | `record` | id, agentId, type, severity, timestamp |
| `AgentStatus` | `record` | agentId, ip, online, lastHeartbeat |
| `User` | `record` | id, username, password, role |
| `Role` | `enum` | ADMIN, VIEWER |

Tous implémentent `Serializable` pour le transport RMI et UDP.

### 3.2 Constantes réseau (`NetworkConstants.java`)

```java
PORT_UDP  = 5000   // UDP métriques
PORT_TCP  = 6000   // TCP alertes
PORT_RMI  = 1099   // Registre RMI
PORT_REST = 8080   // API Spring Boot
SERVER_HOST = "localhost"
```

### 3.3 Interface RMI partagée (`RMIMetricsService.java`)

```java
Map<String, MetricData> getLatestMetrics()
List<String>            getAgentList()
MetricData              getMetricsByAgent(String agentId)
List<MetricData>        getHistory(String agentId)
List<Alert>             getAlerts()
boolean                 registerAgent(String agentId)
boolean                 unregisterAgent(String agentId)
```

### 3.4 Utilitaires

- **`SerializationUtils`** : sérialisation/désérialisation Java (`ObjectOutputStream`)
- **`Logger`** : logging formaté avec niveaux INFO/WARNING/ERROR

---

## 4. Module `agent`

### 4.1 Architecture interne

```
AgentMain
└── MonitoringAgent
    ├── ScheduledExecutorService (2 threads)
    └── MetricPublisherTask (Runnable, toutes les 5 s)
        ├── SystemMetricsCollector   → MetricData
        ├── UDPSender                → envoi UDP
        └── TCPAlertClient           → envoi alerte TCP (si seuil dépassé)
```

### 4.2 Collecte des métriques (`SystemMetricsCollector`)

Utilise la bibliothèque **OSHI 6.4.0** (OS & Hardware Info) :
- **CPU** : `CentralProcessor.getSystemCpuLoad(500ms)`
- **RAM** : `(total - available) / total × 100`
- **Disk** : moyenne sur tous les systèmes de fichiers montés

### 4.3 Multi-threading

`MonitoringAgent` utilise un `ScheduledExecutorService` avec 2 threads :
- Thread 1 : collecte + envoi UDP (tâche périodique)
- Thread 2 : réservé pour futures extensions (envoi batch, ping)

### 4.4 Configuration (`AgentConfig`)

Lecture depuis `agent.properties` (classpath) avec fallback sur valeurs par défaut.

### 4.5 Seuils d'alerte (côté agent)

| Métrique | Seuil | Sévérité |
|----------|-------|----------|
| CPU      | > 85% | CRITICAL |
| RAM      | > 90% | CRITICAL |
| Disk     | > 95% | CRITICAL |

---

## 5. Module `server`

### 5.1 Architecture interne

```
ServerMain (Spring Boot)
└── ServerInitializer (CommandLineRunner)
    ├── RMI Registry :1099
    │   └── RMIMetricsServiceImpl
    ├── UDPServer (thread daemon) :5000
    │   └── ThresholdEngine → AlertDispatcher
    └── TCPServer (thread daemon) :6000
        └── AlertDispatcher

Spring REST :8080
└── MetricsController (/api/*)
    └── MetricsExporter (CSV/JSON)
```

### 5.2 `ConcurrentDataStore` (Singleton thread-safe)

Stockage en mémoire vive :

```java
Map<String, List<MetricData>> metrics;  // ConcurrentHashMap
List<Alert>                   alerts;   // synchronized List
```

Méthodes clés :
- `save(MetricData)` : ajoute à l'historique de l'agent
- `getLatest()` : dernière métrique par agent
- `getHistory(agentId)` : historique complet
- `addAlert(Alert)`, `getAlerts()`
- `getAgentIds()` : set d'IDs

### 5.3 Réception UDP (`UDPServer`)

- Écoute sur port 5000 en boucle infinie
- Désérialise chaque paquet (`ObjectInputStream`)
- Stocke dans `ConcurrentDataStore`
- Appelle `ThresholdEngine.check()` → `AlertDispatcher.dispatch()` si alerte

### 5.4 Serveur TCP (`TCPServer`)

- Accepte les connexions sur port 6000
- Chaque connexion = 1 thread (`new Thread(handleClient)`)
- Désérialise l'objet `Alert`, l'ajoute au store

### 5.5 Service RMI (`RMIMetricsServiceImpl`)

Implémente `shared/rmi/RMIMetricsService`. Expose :
- Métriques temps réel et historiques
- Liste des agents
- Alertes

Enregistré dans le registre RMI : `rmi://localhost:1099/MetricsService`

### 5.6 API REST (`MetricsController`)

| Endpoint | Retour |
|----------|--------|
| `GET /api/agents` | `List<String>` |
| `GET /api/metrics/latest` | `Map<String, MetricData>` |
| `GET /api/metrics/{id}/history` | `List<MetricData>` |
| `GET /api/alerts` | `List<Alert>` |
| `GET /api/export/csv` | `text/plain` (téléchargement) |
| `GET /api/export/json` | `application/json` (téléchargement) |

### 5.7 `ThresholdEngine`

Évaluation côté serveur en complément de l'agent :
- CPU > 85% → Alert CRITICAL
- RAM > 90% → Alert WARNING
- Disk > 95% → Alert WARNING

### 5.8 Sécurité (`SecurityConfig`)

Spring Security configuré pour autoriser tous les endpoints publics (mode démo académique). CORS ouvert pour le client web.

`AuthService` fournit une authentification simple (admin/viewer) extensible vers Spring Security.

---

## 6. Module `client-desktop`

### 6.1 Architecture MVC

```
DesktopApp (main)
└── DashboardController
    ├── RMIServiceProxy   (accès RMI)
    └── DashboardView     (Swing)
        ├── JTabbedPane
        │   ├── Onglet "Tableau de bord" (JTable + renderer coloré)
        │   ├── Onglet "Alertes"         (JTable + renderer couleur sévérité)
        │   └── Onglet "Export"          (JFileChooser)
        ├── Header (titre + bouton refresh)
        └── StatusBar (état connexion + heure)
```

### 6.2 Code couleur des métriques

| Valeur | Couleur | Signification |
|--------|---------|---------------|
| < 60%  | 🟢 Vert | Normal |
| 60–84% | 🟠 Orange | Avertissement |
| ≥ 85%  | 🔴 Rouge | Critique |

### 6.3 Rafraîchissement

`javax.swing.Timer` à 5000 ms appelle `DashboardController.loadData()` sur l'EDT.

### 6.4 Communication RMI

`RMIServiceProxy` encapsule la connexion au registre RMI et expose :
`getLatestMetrics()`, `getAgentList()`, `getHistory()`, `getAlerts()`

---

## 7. Client Web (`client-web`)

### 7.1 Technologies

| Librairie | Rôle |
|-----------|------|
| React 18  | Framework UI |
| Vite 5    | Build tool + proxy dev |
| axios     | Requêtes HTTP vers `/api` |
| recharts  | Graphiques (BarChart comparatif) |

### 7.2 Architecture React

```
App.jsx
├── État : metrics, alerts, lastUpdate, activeTab
├── useEffect → polling toutes les 5 s (axios GET)
└── Rendu conditionnel par onglet :
    ├── Dashboard : AgentCard[] + BarChart
    ├── Alerts    : table avec badges de sévérité
    └── Export    : boutons téléchargement CSV/JSON
```

### 7.3 Proxy Vite

En développement, `/api/*` est proxifié vers `http://localhost:8080` (pas de CORS).

---

## 8. Flux de données end-to-end

### 8.1 Collecte normale (toutes les 5 s)

```
AgentMain → MonitoringAgent → MetricPublisherTask
  → SystemMetricsCollector.collect()           : MetricData
  → UDPSender.send(MetricData)                 : DatagramPacket [sérialisation Java]
  → UDPServer.receive()                        : désérialisation
  → ConcurrentDataStore.save()                 : stockage
  → ThresholdEngine.check()                    : évaluation seuils
```

### 8.2 Alerte critique

```
MetricPublisherTask.run()
  → CPU/RAM/Disk > seuil
  → TCPAlertClient.sendAlert(Alert)            : TCP port 6000
  → TCPServer.handleClient()                   : désérialisation
  → ConcurrentDataStore.addAlert()             + AlertDispatcher.dispatch()
```

### 8.3 Affichage Desktop

```
DashboardController (Timer 5 s)
  → RMIServiceProxy.getLatestMetrics()         : appel RMI
  → RMIMetricsServiceImpl.getLatestMetrics()   : lecture store
  → DashboardView.updateMetrics()              : mise à jour JTable
  → RMIServiceProxy.getAlerts()                : appel RMI
  → DashboardView.updateAlerts()               : mise à jour onglet alertes
```

### 8.4 Affichage Web

```
App.jsx (setInterval 5 s)
  → axios.get('/api/metrics/latest')           : REST GET
  → MetricsController.getLatest()              : lecture store
  → setMetrics()                               : re-render React
  → axios.get('/api/alerts')                   : REST GET
  → setAlerts()                                : re-render React
```

---

## 9. Diagrammes UML

Les diagrammes sont disponibles dans `docs/uml/` :

| Fichier | Contenu |
|---------|---------|
| `diagrame_use_case1.png` | Cas d'utilisation — Agent & Serveur |
| `diagramme_use_case2.png` | Cas d'utilisation — Interface utilisateur |
| `diagramme_de_class.png` | Diagramme de classes principal |
| `diagramme_sequence.png` | Séquence : collecte → affichage |

### Diagrammes à compléter pour le rapport

1. **Diagramme de composants** : modules Maven + client-web, protocoles
2. **Diagramme de déploiement** : machine agent / serveur / client
3. **Séquence #2** : alerte TCP
4. **Séquence #3** : export données

---

## 10. Persistance et export

### Persistance en mémoire

`ConcurrentDataStore` conserve **tout l'historique en RAM** depuis le démarrage.  
Le fichier `schema.sql` définit les tables H2 (`metric_data`, `alerts`) pour une future persistance via Spring Data.

### Export

`MetricsExporter` (@Component Spring) formate les données en :
- **CSV** : `agentId,cpuUsage,ramUsage,diskUsage,timestamp`
- **JSON** : tableau d'objets JSON

Accessible via REST (`/api/export/csv`, `/api/export/json`) et depuis le client desktop (export fichier local).

---

## 11. Gestion des utilisateurs

`AuthService` fournit deux comptes préconfigurés :

| Utilisateur | Mot de passe | Rôle |
|-------------|-------------|------|
| `admin`     | `admin123`  | ADMIN |
| `viewer`    | `viewer123` | VIEWER |

La logique `login()` et `checkRole()` est implémentée et prête à être intégrée dans Spring Security ou exposée via un endpoint `/api/auth/login`.

---

## 12. Déploiement

### Déploiement local (démo)

```
Machine unique :
  - server-1.0-SNAPSHOT.jar  → port 5000/6000/1099/8080
  - agent-1.0-SNAPSHOT.jar   → client UDP/TCP
  - client-desktop JAR        → connexion RMI localhost
  - client-web npm dev        → http://localhost:5173
```

### Déploiement distribué (production)

```
Machine A (Serveur) : java -jar server.jar
Machine B (Agent)   : agent.properties → server.host=IP_MACHINE_A
Machine C (Client)  : client-desktop → RMI vers IP_MACHINE_A:1099
Navigateur          : client-web → REST vers http://IP_MACHINE_A:8080
```

### Prérequis réseau

| Port | Protocole | Direction | Rôle |
|------|-----------|-----------|------|
| 5000 | UDP | Agent → Serveur | Métriques |
| 6000 | TCP | Agent → Serveur | Alertes |
| 1099 | TCP | Client Desktop ↔ Serveur | RMI |
| 8080 | TCP | Client Web → Serveur | REST API |

