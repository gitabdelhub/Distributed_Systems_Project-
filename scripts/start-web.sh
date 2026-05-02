#!/bin/bash
# Démarre le client web React en mode développement
# Prérequis : Node.js 18+ et npm 9+ installés

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
WEB_DIR="$SCRIPT_DIR/../client-web"

echo "Démarrage du client web React..."
echo "URL : http://localhost:5173"
echo "Proxy vers API : http://localhost:8080"
echo ""

cd "$WEB_DIR"

# Installer les dépendances si node_modules absent
if [ ! -d "node_modules" ]; then
    echo "Installation des dépendances npm..."
    npm install
fi

npm run dev
