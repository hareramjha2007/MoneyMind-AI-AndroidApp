# MoneyMind AI — by Hastradar

> **"Understand your money. Improve your future."**

MoneyMind AI is a production-grade, privacy-first Android application that passively understands a user's financial life from bank, UPI, and expense tracker notifications. It calculates a real-time **Financial Health Score (0–100)**, surfaces behavioral spending insights, subscription radar tracking, interactive goal projections, line-by-line expense drill-downs, an interactive 6-step setup questionnaire, "Can I Afford This?" purchase simulator, native biometric app lock, and an AI financial coach — **without ever asking the user to manually log a single expense**.

---

## Table of Contents
1. [Product Summary & Positioning](#1-product-summary--positioning)
2. [Key Features](#2-key-features)
3. [Architecture & Technical Design](#3-architecture--technical-design)
4. [Environment-Aware Data Seeding & Setup Flow](#4-environment-aware-data-seeding--setup-flow)
5. [Tech Stack & Libraries](#5-tech-stack--libraries)
6. [AI Integration & Google Gemini Engine](#6-ai-integration--google-gemini-engine)
7. [Local Build & Run Instructions](#7-local-build--run-instructions)
8. [Google Play Store Deployment Guide](#8-google-play-store-deployment-guide)
9. [Privacy & Security Compliance](#9-privacy--security-compliance)

---

## 1. Product Summary & Positioning

MoneyMind AI is positioned strictly as a **Behavioral Financial Guidance & Awareness Product**, NOT a transaction-executing fintech, stock broker, or investment advice app.

* **Core Value Proposition**: Traditional expense managers fail because manual logging creates friction. MoneyMind AI operates in the background, listening to transaction notifications from major Indian banks (SBI, HDFC, ICICI, Axis, Kotak), UPI apps (PhonePe, Paytm, Google Pay), and 3rd-party expense trackers (Axio, Walnut, Fold, Spendee).
* **Privacy Guarantees**: Raw notification and SMS texts are parsed locally in memory by Hastradar Regex Engine and discarded immediately. No bank account credentials, credit card numbers, or full SMS logs ever leave the device. All data is encrypted locally using 256-bit AES SQLCipher.

---

## 2. Key Features

### 🔹 100% Passive Financial Tracking & Live Notification Listener
* Monitors incoming transaction notifications from financial apps automatically.
* Extracts amount, merchant/counterparty, category, account last 4 digits, and transaction type (Debit/Credit).
* Built-in support for popular expense tracker notifications (Axio, Walnut, Fold, Spendee).

### 🔹 "Can I Afford This?" Purchase Impact Simulator (`AffordabilitySimulatorSheet.kt`)
* Interactive decision tool rendered on Home Screen under the Hero Score Gauge.
* Simulates the exact financial impact of major purchases (e.g. *MacBook Pro M3 - ₹1,49,900* or *iPhone 16 Pro - ₹1,29,900*) before buying:
  - **Health Score Drop**: Calculates projected score change (e.g., `85 ➔ 74`, `-11 pts`).
  - **Emergency Reserve Runway**: Calculates remaining liquid runway (e.g., `5.8 months ➔ 3.2 months`).
  - **Smart Verdict Badge**: Displays `✅ Safe to Purchase`, `⚠️ Moderate Impact (Wait 30 Days)`, or `🚨 High Risk: Drains Emergency Reserve`.
  - **1-Tap Goal Conversion**: Direct button to convert the simulated item into a long-term Savings Goal.

### 🔹 Life Goal Smart Presets & Milestone Celebration Badges
* **1-Tap Smart Presets**: Auto-fills title & target amount for popular life goals (*MacBook Pro M3*, *iPhone 16 Pro*, *Emergency Reserve*, *Electric Vehicle*, *Dream Vacation*).
* **Milestone Celebrations**: Renders visual progress badges on goal cards for `25%`, `50%`, `75%`, and `🏆 100% Goal Achieved` milestones.

### 🔹 Interactive 6-Step Financial Setup Questionnaire & Persistence
* Captures baseline financial metrics on first launch:
  1. **Welcome & Privacy Commitment** (100% Encrypted & Local Vault)
  2. **Monthly In-Hand Income** (Quick-select chips + numeric input)
  3. **Emergency Cash Reserves** (Liquid savings status & target)
  4. **Health Insurance Cover** (Corporate / Personal policy & sum assured)
  5. **Term Life Insurance Cover** (Family protection & cover amount)
  6. **Fixed Monthly Loan EMIs** (Debt obligations)
* **Single-Time Setup Persistence**: Saved locally in `SharedPreferences` via `UserPreferencesRepository`. Onboarding runs once on first launch and is remembered on subsequent launches.

### 🔹 Financial Vault & Baseline Health Score (0–100)
* Calculates a daily-updated health score based on 5 weighted pillars:
  1. **Savings Ratio (25%)**: Ratio of net monthly savings to income.
  2. **Spending Consistency (20%)**: Discretionary spending variance across weeks.
  3. **Emergency Reserve (20%)**: Months of essential expenses saved toward target.
  4. **Debt-to-Income (20%)**: Monthly EMI and credit obligations vs income.
  5. **Subscription Waste (15%)**: Active vs unwanted recurring monthly charges.
* **Day 1 Score Gauge**: Even before bank notifications arrive, MoneyMind AI calculates your baseline health score from setup responses so the Hero Score Gauge Card is displayed immediately.

### 🔹 Editable Financial Vault Data Screen & Top-Left Profile Icon (`👤`)
* Access setup figures anytime via the **Top-Left Profile Button (`👤`)** on any main screen.
* Tap any baseline figure (Salary, Emergency Reserve, Insurance, EMIs) to edit it inline anytime **without re-running onboarding**.

### 🔹 Streamlined 4-Tab Navigation & Back Button App Exit
* Clean 4-tab bottom navigation bar: **Home**, **Insights**, **Goals**, **Coach**.
* **Clean System Back Exit**: Added `BackHandler` on `HomeScreen` so pressing the system Back button closes/exits the application cleanly instead of popping back to onboarding.

### 🔹 Native Biometric App Lock (`BiometricAuthHelper`)
* Integrated Android `androidx.biometric.BiometricPrompt`.
* Protects financial vault data with fingerprint, face unlock, or device PIN.
* App launch gate presents a secure lock screen overlay until authentication succeeds.

### 🔹 Conversational AI Financial Coach & High-Intent Prompt Pills
* Chat interface with streaming AI responses powered by **Google Gemini 3.6 Flash**.
* Includes 1-tap question prompt pills:
  - `💡 Can I afford a MacBook Pro?`
  - `📉 Why did my Financial Health Score drop?`
  - `✂️ Which subscription should I cancel?`
  - `🚀 How do I boost my score above 85?`
* Strict safety guardrails: zero investment advice, zero stock picks, zero numeric return promises.

### 🔹 Brand Identity, Vector Launcher Icon & 14-Day Free Trial Default
* Mobile Launcher Icon (`ic_launcher_foreground.xml`) featuring the **Hastradar Financial AI Emblem** (Concentric Pulse Rings + Upward Financial Growth Arrow + AI Spark Star).
* Default subscription plan is **14-Day Free Trial (Full Access)**.
* **Clear Local Encrypted Database** button in Settings purges all Room database tables instantly.

---

## 3. Architecture & Technical Design

The project is structured according to **Google's Recommended Android Architecture Guide** using multi-module Clean Architecture with **MVI / MVVM** patterns.

```
MoneyMind AI Architecture
├── :app (Application entry, Navigation Host, Animated Splash Screen, Biometric Gate, Launcher Icons, Hilt Root)
├── :core-domain (Models, Use Cases, Repository Interfaces, AI Interfaces, Billing Enums)
├── :core-data (Room Database, SQLCipher, DAOs, Notification Listener, UserPreferencesRepository, BiometricAuthHelper, Gemini AI Engine)
├── :core-ui (Design System, Tokens, Components, Hastradar Emblem, Affordability Simulator, Theme, Paywall Sheet)
├── :feature-home (Home Screen, Score Gauge Card, Can I Afford This Simulator Card, Dynamic AI Strategy, BackHandler Exit)
├── :feature-insights (Behavioral Insights, Score Factors Breakdown, Subscription Radar, Expense Drill-Down Sheet)
├── :feature-goals (Goal List, Milestone Badges, Smart Preset Autofill, Add Goal Modal, Target Projections)
├── :feature-coach (AI Coach Chat Screen, High-Intent Prompt Pills, Gemini 3.6 Flash Integration)
└── :feature-profile (Financial Vault Data Editor, Inline Metric Editing, Biometric Lock Toggle, Database Erasure Flow)
```

---

## 4. Environment-Aware Data Seeding & Setup Flow

To ensure an optimal experience for both developers and real mobile users:

* **Single-Time Questionnaire Persistence**:
  - Saved in Android `SharedPreferences` via `UserPreferencesRepositoryImpl`.
  - Onboarding runs on first launch and routes directly to `HomeScreen` on subsequent app launches.
* **Real Physical Mobile Device (`DeviceUtils.isEmulator() == false`)**:
  - When installed on a user's real smartphone, no dummy seed data is injected.
  - The database starts 100% clean. As real bank notifications arrive via `MoneyMindNotificationListenerService`, real user transactions are parsed and saved locally.

---

## 5. Tech Stack & Libraries

| Layer | Technology / Library | Description |
|---|---|---|
| **Language** | Kotlin 1.9+ | 100% Kotlin codebase |
| **UI Framework** | Jetpack Compose + Material 3 | Modern declarative UI with rich dark mode aesthetics |
| **Architecture** | Clean Architecture (Multi-module) | Decoupled domain, data, UI, and feature modules |
| **Dependency Injection** | Dagger Hilt | Compile-time dependency injection |
| **Database** | Room + SQLCipher | Encrypted local SQLite database (256-bit AES) |
| **Security & Auth** | `androidx.biometric:biometric` | Native Fingerprint, Face Unlock & Device PIN authentication |
| **Preferences** | Android `SharedPreferences` | Persistent onboarding setup & security settings |
| **Asynchronous** | Kotlin Coroutines & Flow | Asynchronous reactive streams |
| **Navigation** | Navigation Compose | Type-safe declarative screen routing |
| **Background Processing** | Android `NotificationListenerService` | Passive background notification parsing |
| **AI Engine** | Google Gemini 3.6 Flash SDK (`com.google.ai.client.generativeai`) | Official Generative AI SDK for streaming responses |

---

## 6. AI Integration & Google Gemini Engine

### 🧠 How AI Coach Architecture Works

* **Zero User Setup Required**: The end user **never** configures or sees any API keys. As the company (**Hastradar**), the Google Gemini API key is managed securely inside the application build environment (`BuildConfig.GEMINI_API_KEY`) or routed through our secure backend proxy.
* **Live Gemini 3.6 Flash Integration (`GeminiCoachProviderImpl`)**: The app uses the official Google AI SDK (`com.google.ai.client.generativeai`) to stream real-time responses from **Gemini 3.6 Flash**.
* **Setup Profile Context Ingestion**: Formats income, emergency reserve, health insurance cover, term life cover, and fixed loan EMIs into Gemini prompt context.
* **System Prompt Guardrails (`MoneyMindSystemPrompt`)**: Every request is wrapped with strict behavioral guardrails:
  1. Grounded strictly in anonymized spending/savings metrics (`FinancialSummary`).
  2. Absolute prohibition against investment advice, stock recommendations, or return guarantees.
* **Resilient Connectivity Protection**: If internet connection is lost, the engine seamlessly falls back to local financial heuristics (`CloudFunctionCoachProxyImpl`) so the user interface remains 100% responsive without errors.

---

## 7. Local Build & Run Instructions

### Prerequisites
* **Android Studio**: Jellyfish / Koala or newer.
* **JDK**: Version 17 or higher.
* **Android SDK**: API 34 (Android 14.0 / UpsideDownCake).
* **Emulator**: Android Virtual Device (AVD) running API 31+ (Android 12+ recommended).

### Quick 1-Step Execution (`run.sh`)
An executable shell script [`run.sh`](file:///Users/hareramjha/Developer/MoneyMind/run.sh) is included at the project root to clean, assemble, install, and launch the app on a connected emulator automatically:

```bash
chmod +x run.sh
./run.sh
```

---

## 8. Google Play Store Deployment Guide

Follow this comprehensive guide to prepare, sign, configure policies, and publish **MoneyMind AI** on the Google Play Store.

### Step 1: Generate Release Keystore
Generate a secure Java KeyStore (`.jks`) file for signing production APKs/App Bundles:

```bash
keytool -genkey -v -keystore moneymind-release-key.jks \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias moneymind-key-alias
```

### Step 2: Generate Android App Bundle (.aab)
Build the production Android App Bundle:

```bash
./gradlew bundleRelease
```
The generated bundle will be located at:
`app/build/outputs/bundle/release/app-release.aab`

---

## 9. Privacy & Security Compliance

* **SQLCipher DB Encryption**: Local Room database encrypted using 256-bit AES encryption.
* **Biometric Vault Gate**: Fingerprint & Device PIN lock protection.
* **Zero SMS Log Retention**: Raw notification texts are processed strictly in RAM and garbage-collected immediately.
* **User Control**: Full 1-click database purge available under **Vault Data -> Clear Local Encrypted Database**.
* **Developed By**: **Hastradar**

---

© 2026 Hastradar. All Rights Reserved.
