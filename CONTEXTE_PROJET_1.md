# Ligne de 5 — Contexte du projet

Ce document résume toutes les décisions prises pendant la phase de prototypage avec Claude (claude.ai), pour que Claude Code puisse reprendre le projet sans qu'il soit nécessaire de tout réexpliquer.

## Concept du jeu

Solitaire de puzzle géométrique. Le joueur ajoute des points sur une grille pour former des lignes de 5 points alignés (horizontal, vertical, ou diagonal), jusqu'à être bloqué.

## Configuration de départ

La grille de départ est une forme en "pinwheel" à 4 branches identiques tournées à 90°, formant une boucle fermée de 36 points. Coordonnées exactes (voir `initialPoints` dans le code) :

```
[0,0],[0,1],[0,2],[0,3],[1,3],[2,3],[3,3],[3,2],[3,1],[3,0],
[4,0],[5,0],[6,0],[6,-1],[6,-2],[6,-3],[5,-3],[4,-3],[3,-3],
[3,-4],[3,-5],[3,-6],[2,-6],[1,-6],[0,-6],[0,-5],[0,-4],[0,-3],
[-1,-3],[-2,-3],[-3,-3],[-3,-2],[-3,-1],[-3,0],[-2,0],[-1,0]
```

Le point (0,0) n'a plus de statut particulier dans le jeu (visuellement neutre) — c'était uniquement un repère utilisé pendant la conception.

## Règles précises (points subtils, déjà débogués)

1. **Placement** : le joueur clique sur une intersection vide de la grille (qui s'étend automatiquement au-delà de la structure existante).

2. **Validité d'un coup** : le nouveau point doit faire partie d'une fenêtre de 5 points alignés consécutifs (horizontal/vertical/diagonal) dont les 4 autres points sont déjà occupés. **Important** : le nouveau point peut se trouver à n'importe quelle position dans cette fenêtre de 5 (extrémité OU au milieu) — ce n'est pas limité à une extension en bout de ligne. C'est le point le plus subtil de la logique (voir fonction `getValidLinesForPoint`, qui teste les 5 positions possibles du nouveau point dans la fenêtre, sur les 4 axes).

3. **Règle du point partagé** : une nouvelle ligne ne peut partager qu'un seul point maximum avec une ligne déjà validée. Si elle en partage 2 ou plus avec une ligne existante, elle est invalide et n'apparaît pas comme option.

4. **Choix multiple** : si un point complète plusieurs lignes valides simultanément, le joueur doit choisir laquelle valider (bouton radio, choix exclusif — jamais plusieurs lignes validées d'un coup). Un panneau affiche chaque option avec sa direction et ses coordonnées d'extrémités. Les lignes candidates sont affichées avec un léger décalage perpendiculaire pour rester visuellement distinctes quand elles se chevauchent.

5. **Annulation** : bouton "Annuler le dernier coup" — ne permet de revenir qu'sur le tout dernier coup joué, pas au-delà (un seul niveau d'undo).

6. **Fin de partie** : quand aucun coup valide n'existe nulle part sur la grille (recherche sur une portée de 4 cases par axe autour de chaque point occupé), la partie se termine et affiche le score final.

## État actuel du prototype

Fichier unique autonome : `index.html` (HTML + CSS + JS vanilla, pas de dépendance de build, SVG généré dynamiquement en DOM).

**Fonctionnalités implémentées :**
- Logique de jeu complète et testée (voir règles ci-dessus)
- Rendu SVG dynamique avec grille qui s'étend automatiquement
- Choix multiple avec panneau de sélection radio
- Annulation du dernier coup (un seul niveau)
- Case à cocher "Afficher les coups possibles" (indice visuel des intersections jouables)
- Animations : apparition du point (rebond), tracé progressif de la ligne validée à la manière d'un trait de plume, pulsation des lignes candidates, "bump" du score, écran de fin façon tampon encré
- Son : effets sonores via Web Audio API (pas de fichiers audio externes), bouton pour couper le son

**Identité visuelle :** thème "coffret de jeu en bois" —
- Fond : texture bois générée procéduralement par `<canvas>` (veinure, nœuds, vignettage), régénérée au chargement et au redimensionnement
- Points : billes turquoise avec dégradé radial (effet verre/marbre)
- Lignes validées : couleur cuivre/ambre ("brûlée" dans le bois)
- Cadre du plateau et boutons : laiton
- Typographie : Space Grotesk (titres), IBM Plex Sans (texte), IBM Plex Mono (chiffres HUD) — chargées via Google Fonts CDN

## Feuille de route pour le déploiement (à faire avec Claude Code)

### 1. Empaquetage mobile
- Utiliser **Capacitor** pour empaqueter ce HTML/JS en application native iOS/Android
- Compte développeur Apple : 99 $/an (nécessite un Mac pour Xcode, ou un service de build cloud comme Codemagic si pas de Mac disponible)
- Compte développeur Google Play : 25 $ une fois

### 2. Monétisation
- **Abonnement débloquant "Afficher les coups possibles"** (actuellement gratuit dans le prototype — à transformer en fonctionnalité premium)
- **Publicités + abonnement pour les retirer**
- Implémentation via StoreKit (Apple) et Play Billing (Google)
- Penser à activer le "Small Business Program" chez Apple et Google pour la commission réduite à 15% (au lieu de 30%) — non automatique, à activer manuellement dans les consoles développeur

### 3. Record personnel et classement mondial/pays (décision à prendre)
Deux options, à trancher ensemble :
- **Game Center (iOS) / Google Play Games Services (Android)** — solution rapide, classements et sauvegarde du meilleur score fournis nativement, sans backend à héberger
- **Backend maison** (Firebase, Supabase, etc.) — plus de travail, mais un classement unifié entre iOS et Android au même endroit plutôt que deux classements séparés

### 4. Améliorations visuelles possibles
- ~~Défi du jour~~ — écarté, pas de nécessité identifiée
- ~~Tutoriel intégré pour les premiers coups~~ — fait (overlay en 5 étapes, rejouable via bouton "?")

## Préférences de travail d'Eric

- Ne code pas lui-même — dépend de Claude Code pour toute évolution
- Souhaite garder la main sur le projet : code à conserver sur GitHub (portable, pas de dépendance à un prestataire)
- Attentif à la qualité de la logique de jeu — a testé et fait corriger plusieurs subtilités de règles avant de valider (voir section "Règles précises" ci-dessus, où plusieurs bugs ont déjà été identifiés et corrigés)
