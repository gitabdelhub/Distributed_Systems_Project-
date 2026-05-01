# Guide d'utilisation — Système de Surveillance Distribué

> **Cours :** Systèmes Distribués | **Année :** 2025-2026

---

## Table des matières

1. [Prérequis](#1-prérequis)
2. [Build du projet](#2-build-du-projet)
3. [Lancement du système](#3-lancement-du-système)
4. [Configuration](#4-configuration)
5. [Client Desktop (Swing / RMI)](#5-client-desktop-swing--rmi)
6. [Client Web (React / REST)](#6-client-web-react--rest)
7. [API REST — référence complète](#7-api-rest--référence-complète)
8. [Scénario de démonstration (10 min)](#8-scénario-de-démonstration-10-min)
9. [Dépannage](#9-dépannage)

---

## 1. Prérequis

| Outil | Version minimale | Vérification |
|-------|-----------------|--------------|
| JDK (Java) | **17** | `java -version` |
| Maven | **3.8** | `mvn -version` |
| Node.js | **18** | `node -v` |
| npm | **9** | `npm -v` |

---

## 2. Build du projet

### Build Java (Maven multi-module)

```bash
# Depuis la racine du projet
bash scripts/build-all.sh
# Équivalent à :
mvn clean install -DskipTests
```

Cela compile et package dans l'ordre :
1. `shared` → `shared/target/shared-1.0-SNAPSHOT.jar`
2. `agent` → `agent/target/agent-1.0-SNAPSHOT.jar`
3. `server` → `server/target/server-1.0-SNAPSHOT.jar`
4. `client-desktop` → `client-desktop/target/client-desktop-1.0-SNAPSHOT.jar`

### Build client web (npm)

```bash
cd client-web
npm install
npm run build   # production build dans client-web/dist/
```

---

## 3. Lancement du système

### Ordre de démarrage recommandé

```
1. Serveur → 2. Agent(s) → 3. Interface (desktop ou web)
```

### 3.1 Démarrer le serveur central

```bash
bash scripts/start-server.sh
# Équivalent à :
java -jar server/target/server-1.0-SNAPSHOT.jar
```

Le serveur démarre :
- **Spring Boot REST** sur `http://localhost:8080`
- **Listener UDP** sur le port `5000` (réception métriques)
- **Serveur TCP** sur le port `6000` (réception alertes)
- **Registre RMI** sur le port `1099` (client desktop)

Logs attendus :
```
[RMI] Service lié : rmi://localhost:1099/MetricsService
[UDP] Listener démarré sur port 5000
[TCP] Serveur démarré sur port 6000
Tomcat started on port 8080
```

### 3.2 Démarrer un agent

```bash
bash scripts/start-agent.sh
# Équivalent à :
java -jar agent/target/agent-1.0-SNAPSHOT.jar
```

L'agent démarre et envoie des métriques toutes les 5 secondes :
```
[AgentConfig] Configuration chargée depuis agent.properties
[MonitoringAgent] Démarrage agent ID = <UUID>
[UDPSender] Métriques envoyées → localhost:5000
```

Vous pouvez lancer **plusieurs agents en parallèle** dans des terminaux différents.

### 3.3 Lancer le client desktop

```bash
java -jar client-desktop/target/client-desktop-1.0-SNAPSHOT.jar
```

Une fenêtre Swing s'ouvre avec 3 onglets : **Tableau de bord**, **Alertes**, **Export**.

### 3.4 Lancer le client web

```bash
cd client-web
npm install       # première fois seulement
npm run dev       # serveur de développement
# Ouvrez http://localhost:5173 dans votre navigateur
```

---

## 4. Configuration

### Agent — `agent/src/main/resources/agent.properties`

```properties
agent.server.host=localhost        # Adresse IP du serveur central
agent.server.port.udp=5000         # Port UDP pour les métriques
agent.server.port.tcp=6000         # Port TCP pour les alertes
agent.send.interval.ms=5000        # Intervalle d'envoi en millisecondes
```

**Exemple : agent sur machine distante**
```properties
agent.server.host=192.168.1.100
agent.server.port.udp=5000
agent.server.port.tcp=6000
agent.send.interval.ms=3000
```

### Seuils d'alerte — `shared/constants/ThresholdConstants.java`

| Métrique | Seuil agent | Seuil serveur |
|----------|------------|---------------|
| CPU      | 85 %       | 85 %          |
| RAM      | 90 %       | 90 %          |
| Disque   | 95 %       | 95 %          |

---

## 5. Client Desktop (Swing / RMI)

### Onglet « Tableau de bord »

Affiche en temps réel les métriques de chaque agent connecté :

| Colonne | Description |
|---------|-------------|
| Agent ID | UUID unique de l'agent |
| CPU (%) | Utilisation processeur — **vert < 60 %, orange 60-84 %, rouge ≥ 85 %** |
| RAM (%) | Utilisation mémoire vive |
| Disque (%) | Utilisation moyenne des disques |
| État | Normal / Avertissement / Critique |
| Timestamp | Heure de la dernière métrique |

Rafraîchissement automatique toutes les **5 secondes**.  
Clic sur **⟳ Rafraîchir** pour forcer une mise à jour immédiate.

### Onglet « Alertes »

Liste de toutes les alertes reçues, les plus récentes en tête :
- **Rouge** : alerte CRITICAL (CPU > 85 %, RAM > 90 %, Disk > 95 %)
- **Orange** : alerte WARNING

### Onglet « Export »

- **Exporter CSV** → dialogue de sauvegarde → fichier `metrics_export.csv`
- **Exporter JSON** → dialogue de sauvegarde → fichier `metrics_export.json`

Les exports contiennent l'historique complet de toutes les métriques reçues depuis le démarrage du serveur.

---

## 6. Client Web (React / REST)

Accessible sur **http://localhost:5173** après `npm run dev`.

Se connecte au serveur via proxy Vite → `http://localhost:8080/api`.

### Onglet « Tableau de bord »

- **Cartes agents** : une carte par agent avec barres de progression colorées
- **Graphique comparatif** : histogramme Recharts CPU/RAM/Disk pour tous les agents
- Rafraîchissement automatique toutes les **5 secondes**

### Onglet « Alertes »

Table des alertes avec badge de sévérité (CRITICAL / WARNING).

### Onglet « Export »

- **Exporter CSV** → téléchargement `metrics.csv`
- **Exporter JSON** → téléchargement `metrics.json`

---

## 7. API REST — référence complète

Base URL : `http://localhost:8080/api`

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/api/agents` | Liste des IDs d'agents actifs |
| GET | `/api/metrics/latest` | Dernières métriques de tous les agents |
| GET | `/api/metrics/{agentId}/history` | Historique complet d'un agent |
| GET | `/api/alerts` | Toutes les alertes enregistrées |
| GET | `/api/export/csv` | Export CSV de toutes les métriques |
| GET | `/api/export/json` | Export JSON de toutes les métriques |

### Exemples curl

```bash
# Lister les agents actifs
curl http://localhost:8080/api/agents

# Dernières métriques
curl http://localhost:8080/api/metrics/latest | python3 -m json.tool

# Historique d'un agent
curl http://localhost:8080/api/metrics/<agentId>/history

# Alertes
curl http://localhost:8080/api/alerts

# Export CSV
curl http://localhost:8080/api/export/csv -o metrics.csv

# Export JSON
curl http://localhost:8080/api/export/json -o metrics.json
```

---

## 8. Scénario de démonstration (10 min)

| # | Action | Commande / Observation |
|---|--------|------------------------|
| 1 | Build complet | `bash scripts/build-all.sh` → **BUILD SUCCESS** |
| 2 | Démarrer le serveur | `bash scripts/start-server.sh` → logs UDP/TCP/RMI/HTTP |
| 3 | Démarrer 1 agent | `bash scripts/start-agent.sh` → logs métriques |
| 4 | Vérifier via REST | `curl localhost:8080/api/agents` → UUID agent |
| 5 | Voir métriques en direct | `curl localhost:8080/api/metrics/latest` |
| 6 | Ouvrir client desktop | Tableau de bord → ligne colorée avec CPU/RAM/Disk |
| 7 | Ouvrir client web | `npm run dev` → http://localhost:5173 → carte agent + graphique |
| 8 | Démarrer 2ème agent | 2ème terminal → 2 agents dans les interfaces |
| 9 | Provoquer une alerte | Stress CPU (`stress --cpu 4 --timeout 30`) ou attendre seuil |
| 10 | Voir les alertes | Onglet Alertes (desktop + web) → ligne rouge |
| 11 | Export données | Onglet Export → CSV téléchargé |

---

## 9. Dépannage

### Le serveur ne démarre pas : `Address already in use`
Un port est déjà utilisé. Vérifiez et libérez les ports :
```bash
# Linux/Mac
lsof -i :5000   # UDP
lsof -i :6000   # TCP
lsof -i :1099   # RMI
lsof -i :8080   # REST
```

### Le client desktop affiche « Non connecté »
- Vérifiez que le serveur est démarré **avant** le client
- Le registre RMI doit être actif sur `localhost:1099`

### Le client web affiche une erreur réseau
- Vérifiez que le serveur Spring Boot est démarré (`localhost:8080`)
- Vérifiez la configuration proxy dans `vite.config.js`

### L'agent n'envoie pas de métriques
- Vérifiez `agent.properties` : `agent.server.host` doit pointer vers le serveur
- Vérifiez que le port UDP 5000 est ouvert (pare-feu)

