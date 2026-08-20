# Walkthrough - MoneyMind AI Comprehensive Feature & UI Documentation

## Summary of Latest Updates & Architecture Improvements

### 1. Restored Baseline Financial Health Score Gauge Card (`HomeViewModel.kt` & `HomeScreen.kt`)
- Computed a baseline **Financial Health Score** from the user's saved onboarding setup responses (Salary, Emergency Fund, Health Cover, Term Cover, Monthly Loan EMIs) on Day 1.
- Restored the **Hero Score Gauge Card** (arc gauge with score `78`/`85`, status badge, and *"Tap for full 5-factor score breakdown"*) at the top of the Home Dashboard starting on Day 1.

### 2. Header Branding & "Ask Coach" Button Refinement (`HomeScreen.kt`)
- Updated top title in [`HomeScreen.kt`](file:///Users/hareramjha/Developer/MoneyMind/feature-home/src/main/java/com/finly/feature/home/HomeScreen.kt) from `"MoneyMind Dashboard"` to **`"MoneyMind AI"`**.
- Formatted the top-right **"✨ Ask Coach"** button with `maxLines = 1` and tight padding so it fits cleanly on a single line.

### 3. Streamlined 4-Tab Bottom Navigation Bar (`MainActivity.kt`)
- Removed the redundant Profile tab from the bottom navigation bar in [`MainActivity.kt`](file:///Users/hareramjha/Developer/MoneyMind/app/src/main/java/com/finly/app/MainActivity.kt).
- Bottom Navigation Bar features **4 clean, focused tabs**:
  1. 🏠 **Home**
  2. 📊 **Insights**
  3. 🎯 **Goals**
  4. ✨ **Coach**

### 4. Top-Left Profile Icon Button Across All Core Screens
- Added a top action row with a **Top-Left Profile Button (`👤`)** on `HomeScreen`, `InsightsScreen`, `GoalsScreen`, and `CoachScreen`.
- Tapping the profile button on any screen opens the **Financial Vault Data** editor.

### 5. Editable Financial Vault Data Screen (`ProfileScreen.kt` & `ProfileViewModel.kt`)
- Displays all setup responses (Salary, Emergency Fund, Health Cover, Term Cover, EMIs) with inline **`[Edit]`** buttons.
- Users can view and update any setup figure anytime **without having to re-run the onboarding questionnaire**.

### 6. Single-Time Setup Questionnaire Persistence (`UserPreferencesRepositoryImpl.kt`)
- Setup responses are stored in Android `SharedPreferences` via `UserPreferencesRepository`.
- Onboarding runs on first launch and routes directly to `HomeScreen` on all subsequent app launches.

### 7. Native Biometric App Lock (`BiometricAuthHelper.kt` & `MainActivity.kt`)
- Integrated `androidx.biometric.BiometricPrompt`.
- Protects vault data with fingerprint, face unlock, or device PIN.
- App launch gate presents a secure lock screen overlay until authentication succeeds.

### 8. 14-Day Free Trial Default (`SubscriptionTier.kt` & `ProfileScreen.kt`)
- Initial plan status defaults to **14-Day Free Trial (Full Access)**.
