# 📚 KaniCard - Simple Flashcard Learning App
A lightweight, offline flashcard-based learning application inspired by Anki, built with modern Android technologies.

---

## 📑 Table of Contents
- [Overview](#overview)
- [Screenshots](#screenshots)
- [Features](#features)
- [Architecture and Tech Stack](#architecture-and-tech-stack)
- [Performance and Quality](#performance-and-quality)
- [Getting Started](#getting-started)
- [Contributing](#contributing)
- [License](#license)

---

## 🔎 Overview
KaniCard is a modern flashcard app designed for efficient learning with a focus on simplicity and performance.  
It leverages spaced repetition to optimize memorization and offers an offline-first experience with a clean, intuitive interface.

---

## 🖼 Screenshots
*(Add your app screenshots here)*

---

## 🚀 Features

### 📖 Flashcard Learning
- Create, edit, and delete text-based flashcards  
- Organize cards into decks and subdecks  
- Study with a simple question-to-answer reveal flow  
- Spaced Repetition System (SRS) for optimized learning  

### 📊 Progress Tracking
- Real-time statistics on study performance  
- Visual charts for daily activity and accuracy  
- Track mastered and pending cards  

### 💾 Local Database
- Offline storage using Room database  
- Fast data loading and automatic persistence  
- No internet connection required  

---

## 🏗 Architecture and Tech Stack

### 📐 System Architecture
```mermaid
graph TB
    A[Jetpack Compose UI] --> B[ViewModel Layer]
    B --> C[Repository Pattern]
    C --> D[Room Database]
    M[Hilt Dependency Injection] --> B
    M --> C
````

### 🛠 Technologies

* **Kotlin**: 100% Kotlin-based codebase
* **Jetpack Compose**: Declarative UI framework
* **Room**: Local database for data persistence
* **MVVM**: Clean architecture with separation of concerns
* **Hilt**: Dependency injection for modularity
* **Coroutines & Flow**: Asynchronous and reactive programming

---

## ⚡ Performance and Quality

* Optimized for handling large decks (1,000+ cards)
* Lifecycle-aware components to prevent memory leaks
* Smooth animations and transitions using Jetpack Compose
* Unit tests for SRS logic and repository layer
* 80%+ test coverage for core study flows

---

## 🚦 Getting Started

### ✅ Prerequisites

* Android Studio Hedgehog (2023.1.1 or later)
* Kotlin 1.9.0 or higher
* Gradle 8.0 or higher
* JDK 17 or higher
* Android SDK 24+ (API Level 24 or higher)

### ▶️ Build and Run

```bash
# Clone the repository
git clone https://github.com/nmheeir/KaniCard.git
cd KaniCard

# Build and install
./gradlew assembleDebug
./gradlew installDebug
```

### 📂 Project Structure

```
app/
├── data/               # Repository, DAO, and Entities
├── domain/             # Models and Use Cases
├── presentation/       # Compose UI, ViewModels, Navigation
├── di/                 # Hilt Dependency Injection modules
└── util/               # Utilities and extensions
```

---

## 🏆 Technical Highlights

* **Spaced Repetition Algorithm**: Adapts scheduling based on user performance
* **Statistics & Charts**: Visual feedback using Compose Canvas/Charts
* **Offline-First Design**: Study anywhere without an internet connection
* **Extensible Architecture**: Supports future features like cloud sync

---

## 🤝 Contributing

We welcome contributions to KaniCard!

### 🔄 Development Workflow

```bash
git checkout -b feature/new-feature
./gradlew ktlintCheck
./gradlew test
./gradlew connectedAndroidTest
```

### ✅ Code Review Checklist

* Adheres to Clean Architecture principles
* Unit test coverage > 80% for new features
* UI tests for main study flows
* Performance validation with large decks

---

## 📜 License

This project is licensed under the MIT License.
See the [LICENSE](./LICENSE) file for details.

---

<div align="center">

⭐ If you like **KaniCard**, give it a star!

**Simple. Offline. Effective. Learn smarter with flashcards.**

</div>
