# Présentation — Système de Surveillance Distribué
## Script et plan détaillé — 10 minutes

> **Cours :** Systèmes Distribués | **Année :** 2025-2026

---

## Plan des slides

| # | Slide | Durée |
|---|-------|-------|
| 1 | Titre & contexte | 30 s |
| 2 | Problématique & objectifs | 45 s |
| 3 | Architecture globale | 1 min 30 |
| 4 | Module Agent | 1 min |
| 5 | Serveur central | 1 min 30 |
| 6 | Démo live — métriques & alertes | 1 min 30 |
| 7 | Interface Desktop (Swing) | 30 s |
| 8 | Interface Web (React) | 30 s |
| 9 | Choix technologiques | 1 min |
| 10 | Conclusion & bilan | 30 s |

**Total : ~10 minutes**

---

## Slide 1 — Titre (30 s)

**Texte à l'écran :**
> 🖥 Système de Surveillance Distribué
> Monitoring en temps réel · Java 17 · UDP/TCP/RMI/REST

**Script :**
> « Bonjour à tous. Notre projet porte sur la conception et le développement
> d'un système de surveillance distribué complet, en Java, permettant de
> monitorer en temps réel les ressources système — CPU, RAM et Disque —
> de machines distantes. »

---

## Slide 2 — Problématique & Objectifs (45 s)

**Texte à l'écran :**
- ❓ Comment surveiller plusieurs machines en temps réel ?
- ❓ Comment gérer les alertes critiques ?
- ❓ Comment offrir deux types d'interfaces (Desktop + Web) ?

**Objectifs du projet :**
- ✅ Architecture multi-tiers distribuée
- ✅ Communication réseau : UDP, TCP, RMI, REST
- ✅ Concurrence & thread-safety
- ✅ Interface utilisateur moderne (MVC)

**Script :**
> « La problématique centrale est : comment concevoir une infrastructure
> logicielle capable de collecter les métriques de plusieurs machines,
> détecter les anomalies, alerter en temps réel, et les afficher dans
> deux types d'interfaces — desktop Java et web moderne ? »

---

## Slide 3 — Architecture Globale (1 min 30)

**Schéma à l'écran :**
```
┌─────────────┐  UDP:5000   ┌──────────────────┐   RMI:1099   ┌───────────────┐
│    AGENT    │ ──────────► │                  │ ◄──────────► │ Client Desktop│
│  (Machine)  │  TCP:6000   │ SERVEUR CENTRAL  │              │    (Swing)    │
└─────────────┘ ──────────► │  Spring Boot     │   REST:8080  └───────────────┘
                             │  :8080           │ ◄──────────► ┌───────────────┐
┌─────────────┐              └──────────────────┘              │  Client Web   │
│    AGENT    │ ─────── ►                                      │   (React)     │
│  (Machine)  │                                                └───────────────┘
└─────────────┘
```

**Script :**
> « L'architecture est organisée en trois niveaux :
>
> Niveau 1 — les Agents : installés sur chaque machine à surveiller,
> ils collectent CPU, RAM et Disk toutes les 5 secondes grâce à la
> bibliothèque OSHI, et les envoient par UDP.
>
> Niveau 2 — le Serveur Central : reçoit les métriques via UDP,
> les alertes critiques via TCP, expose les données via RMI pour
> le client desktop, et via REST/JSON pour le client web.
>
> Niveau 3 — les Interfaces : un client Swing communique par RMI,
> un client React communique par REST. »

---

## Slide 4 — Module Agent (1 min)

**Texte à l'écran :**
```
AgentMain
└── MonitoringAgent (ScheduledExecutor)
    └── MetricPublisherTask (toutes les 5 s)
        ├── SystemMetricsCollector (OSHI)
        │   → CPU · RAM · Disk
        ├── UDPSender → port 5000
        └── TCPAlertClient → port 6000 (si seuil dépassé)
```

**Seuils :**
| Métrique | Seuil | Action |
|----------|-------|--------|
| CPU | > 85% | Alerte CRITICAL via TCP |
| RAM | > 90% | Alerte CRITICAL via TCP |
| Disque | > 95% | Alerte CRITICAL via TCP |

**Script :**
> « L'agent est conçu pour être léger et autonome. Il utilise
> un ScheduledExecutorService pour garantir l'envoi périodique.
>
> Pour la collecte, on utilise OSHI — une bibliothèque Java cross-platform
> qui donne accès aux métriques OS sans appels natifs directs.
>
> Si un seuil est dépassé, une alerte est envoyée immédiatement par TCP
> — protocole fiable pour les messages critiques — en plus de l'envoi
> UDP périodique. »

---

## Slide 5 — Serveur Central (1 min 30)

**Texte à l'écran :**
```
ServerMain (Spring Boot)
├── UDPServer :5000       → reçoit métriques → ConcurrentDataStore
│   └── ThresholdEngine   → évalue seuils côté serveur
├── TCPServer :6000       → reçoit alertes agents
├── RMI Registry :1099    → expose RMIMetricsServiceImpl
│   └── getLatestMetrics / getHistory / getAlerts
└── REST API :8080
    ├── GET /api/agents
    ├── GET /api/metrics/latest
    ├── GET /api/metrics/{id}/history
    ├── GET /api/alerts
    └── GET /api/export/{csv|json}
```

**ConcurrentDataStore :**
- Singleton thread-safe (ConcurrentHashMap + synchronized List)
- Stocke tout l'historique en RAM

**Script :**
> « Le serveur est le cœur du système. On a fait le choix de Spring Boot
> pour la couche REST, ce qui nous donne la sérialisation JSON automatique,
> la gestion des beans et un serveur HTTP embarqué.
>
> La double évaluation des seuils — côté agent ET côté serveur — garantit
> qu'aucune alerte n'est manquée même si l'agent envoie un message UDP
> qui serait perdu.
>
> Le ConcurrentDataStore utilise des collections thread-safe de java.util.concurrent
> pour gérer la concurrence des multiples threads UDP et RMI. »

---

## Slide 6 — Démo Live (1 min 30)

**Actions à montrer :**
1. `bash scripts/start-server.sh` → logs UDP/TCP/RMI/HTTP
2. `bash scripts/start-agent.sh` → logs métriques toutes les 5 s
3. `curl localhost:8080/api/metrics/latest` → JSON en direct
4. Ouvrir client desktop → tableau de bord + onglet alertes
5. Ouvrir navigateur `http://localhost:5173` → cartes agents + graphique
6. Export CSV depuis l'interface web → téléchargement fichier

**Script :**
> « Passons à la démo. Je démarre le serveur… on voit les trois composants
> réseau démarrer. Je lance l'agent… on voit les métriques arriver sur le
> serveur. 
>
> Voici le client desktop — trois onglets, le code couleur vert/orange/rouge
> selon le niveau de charge. 
>
> Et voici le client web React, avec les mêmes données, les barres de
> progression et le graphique comparatif. Je clique sur Export CSV… »

---

## Slide 7 — Interface Desktop Swing (30 s)

**Capture d'écran / démo :**

**Fonctionnalités :**
- 3 onglets : Tableau de bord · Alertes · Export
- Code couleur : 🟢 Normal / 🟠 Avertissement / 🔴 Critique
- Rafraîchissement automatique toutes les 5 s via RMI
- Export CSV/JSON avec sélecteur de fichier

**Script :**
> « Le client desktop est construit en Swing avec le pattern MVC.
> Le contrôleur orchestre les appels RMI et le timer. La vue gère
> uniquement le rendu. Le proxy RMI isole les détails réseau. »

---

## Slide 8 — Interface Web React (30 s)

**Capture d'écran / démo :**

**Fonctionnalités :**
- Polling REST toutes les 5 s (axios + useEffect)
- Cartes agents avec barres de progression colorées
- Graphique Recharts comparatif CPU/RAM/Disk
- Onglet alertes avec badges de sévérité
- Export CSV/JSON par téléchargement direct

**Script :**
> « Le client web est une SPA React sans TypeScript pour garder
> le focus sur l'architecture distribuée plutôt que le tooling.
> Vite fournit un proxy dev qui évite les problèmes CORS.
> Recharts rend le graphique comparatif en SVG, accessible et responsive. »

---

## Slide 9 — Choix Technologiques (1 min)

**Tableau récapitulatif :**

| Besoin | Technologie | Justification |
|--------|-------------|---------------|
| Collecte métriques | **OSHI 6.4** | Cross-platform, API Java native, zéro dépendance native |
| Transport métriques | **UDP (port 5000)** | Léger, non-bloquant, acceptable perte occasionnelle |
| Transport alertes | **TCP (port 6000)** | Fiable, livraison garantie pour les messages critiques |
| Interface desktop | **Java RMI** | Transparence réseau, intégration Java native, exigence pédagogique |
| API backend | **Spring Boot REST** | Jackson JSON auto, DI, configuration minimale |
| Stockage | **In-memory (ConcurrentHashMap)** | Simplicité démo + schema H2 prêt pour persistance |
| Interface desktop | **Swing MVC** | Standard Java, pas de dépendance externe |
| Interface web | **React + Vite + Recharts** | Léger, composants réutilisables, graphiques SVG |

**Script :**
> « Chaque choix technique est justifié par un besoin précis.
> UDP pour les métriques car une perte occasionnelle est tolérable —
> la prochaine métrique arrive dans 5 secondes.
> TCP pour les alertes car on ne peut pas se permettre de perdre
> un message critique.
> RMI répond à l'exigence pédagogique de l'énoncé.
> Spring Boot accélère la mise en place de l'API REST sans boilerplate. »

---

## Slide 10 — Conclusion & Bilan (30 s)

**Ce qui a été réalisé :**
- ✅ Architecture distribuée 3 niveaux complète
- ✅ 4 protocoles de communication (UDP, TCP, RMI, REST)
- ✅ Multi-threading et thread-safety
- ✅ 2 interfaces utilisateur (Desktop Swing + Web React)
- ✅ Alertes temps réel avec double vérification
- ✅ Export CSV/JSON
- ✅ Documentation complète (guide + architecture)

**Ce qui pourrait être amélioré :**
- 🔧 Persistance H2/PostgreSQL via Spring Data JPA
- 🔧 WebSocket pour push temps réel côté web
- 🔧 Authentification JWT sur l'API REST
- 🔧 Docker Compose pour le déploiement

**Script :**
> « Pour conclure, ce projet nous a permis de mettre en pratique les
> concepts fondamentaux des systèmes distribués : communication réseau
> multi-protocoles, concurrence, architecture en couches et interfaces
> utilisateur séparées du backend.
>
> Les pistes d'amélioration principales sont la persistance base de données,
> les WebSockets pour le push temps réel, et la containerisation Docker.
> Merci pour votre attention, nous sommes disponibles pour vos questions. »

---

## Annexe — Questions fréquentes

**Q : Pourquoi pas WebSocket pour le client web ?**
> UDP et polling REST suffisent pour un intervalle de 5 secondes. WebSocket
> serait justifié pour un intervalle < 1 seconde. C'est une extension prévue.

**Q : Pourquoi pas Spring Data JPA ?**
> Le stockage in-memory simplifie la démo. Le schéma SQL est déjà défini
> (schema.sql), l'intégration JPA nécessiterait d'ajouter les annotations
> @Entity sur les records (ou des classes wrapper).

**Q : Comment scaler l'agent sur 50 machines ?**
> L'architecture est déjà prête : chaque machine lance son agent avec
> l'adresse du serveur dans agent.properties. Le serveur gère n agents
> en parallèle grâce au ConcurrentDataStore.

**Q : La sécurité de l'API REST ?**
> Pour la démo, Spring Security est configuré en mode permissif.
> En production : JWT Bearer token sur tous les endpoints /api/**,
> HTTPS, et restriction CORS à l'URL du client.
