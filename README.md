# PolyBooster

**PolyBooster** est une application Android développée en **Kotlin** qui propose un système de **boosters de cartes de vocabulaire** pour apprendre de nouvelles langues de manière ludique. Les utilisateurs peuvent débloquer des cartes chaque jour, les collectionner par thématiques, et tester leurs connaissances à travers des **quizz dynamiques**.

> Pensée pour allier **jeu** et **apprentissage**, cette app transforme l’étude du vocabulaire en une expérience motivante et progressive.

---

## Fonctionnalités principales

- **Ouverture quotidienne d’un booster de 5 cartes** (mot en français + traduction en anglais/espagnol)
- **Collection organisée par portfolios thématiques** (alimentation, transport, géographie, etc.)
- **Mode quiz** basé sur les cartes débloquées pour réviser en s’amusant
- **Visualisation et recherche dans la collection** de cartes
- Stockage local avec **SQLite** (via Room)

---

## Technologies

- **Langage** : Kotlin (API 21+)
- **Base de données** : Room / SQLite
- **Architecture** : MVVM simplifié
- **UI** : XML Layout + Material Design

---

## Développement

Ce projet a été majoritairement réalisé en solo dans le cadre d’un apprentissage intensif de **Kotlin Android**, en mettant en œuvre :
- La persistance locale via Room
- La gestion d’activités et fragments
- Les animations de transition entre vues
- Un début de logique gamifiée pour l’apprentissage

### Contribution

- 0xAdafanf : Partie logique (backend, partie Kotlin et XML de base, debug)
- IvRdz : Partie esthétique (front XML, effet sonore)

---

## Installation

1. Cloner le dépôt :

bash
git clone https://github.com/0xAdafang/PolyBooster.git

3. Ouvrir le projet dans Android Studio

4. Lancer l’émulateur ou un appareil Android API 21+

---

# PolyBooster

**PolyBooster** is an Android app developed in **Kotlin** that offers a **vocabulary card booster system** to learn new languages in a fun and engaging way. Users can unlock cards daily, organize them into themed portfolios, and test their knowledge through **dynamic quizzes**.

> Designed to combine **gaming** and **learning**, this app turns vocabulary study into a motivating and progressive experience.

---

##  Main Features

- **Daily booster opening with 5 cards** (word in French + English/Spanish translation)
- **Themed portfolio organization** (food, transport, geography, etc.)
- **Quiz mode** based on unlocked cards for interactive review
- **Card collection view and search**
-  Local storage using **SQLite** (via Room)

---

## Technologies

- **Language**: Kotlin (API 21+)
- **Database**: Room / SQLite
- **Architecture**: Simplified MVVM
- **UI**: XML Layout + Material Design

---

## Development

This project was primarily developed solo as part of an intensive learning process in **Kotlin Android**, featuring:
- Local data persistence with Room
- Activity and fragment management
- View transition animations
- Early implementation of gamification logic

### Contribution

- 0xAdafang: Core logic (Kotlin backend, base XML layout, debugging)
- IvRdz: Aesthetic enhancements (UI polish, sound effects)

---

## Installation

1. Clone the repository:
   
bash

git clone https://github.com/0xAdafang/PolyBooster.git

2. Open the project in Android Studio

3. Run the app on an emulator or a physical device (API 21+)
