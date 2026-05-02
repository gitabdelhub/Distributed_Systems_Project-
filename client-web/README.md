# Client Web — Système de Surveillance Distribué

Interface web React pour visualiser en temps réel les métriques collectées par le serveur central.

## Démarrage rapide

```bash
# Depuis la racine du projet
bash scripts/start-web.sh

# Ou manuellement :
cd client-web
npm install   # première fois seulement
npm run dev
```

➡ Ouvrir **http://localhost:5173**

## Prérequis

- Node.js 18+ et npm 9+
- Le **serveur central** doit être démarré sur `localhost:8080`

## Fonctionnalités

| Onglet | Description |
|--------|-------------|
| 📊 Tableau de bord | Cartes agents avec barres CPU/RAM/Disk colorées + graphique Recharts |
| 🚨 Alertes | Liste des alertes CRITICAL/WARNING avec badges de sévérité |
| 📤 Export | Téléchargement CSV ou JSON de toutes les métriques historiques |

## Connexion au serveur

Le client se connecte via proxy Vite au serveur Spring Boot :

- **REST API** : `GET /api/metrics/latest`, `/api/alerts`, `/api/export/csv`, `/api/export/json`
- **Polling** : toutes les 5 secondes (axios + React useEffect)
- **Proxy dev** : `/api/*` → `http://localhost:8080` (configuré dans `vite.config.js`)

## Build de production

```bash
npm run build   # Génère dist/
npm run preview # Prévisualise le build
```

## Stack

- React 18 · Vite 5 · axios · Recharts

