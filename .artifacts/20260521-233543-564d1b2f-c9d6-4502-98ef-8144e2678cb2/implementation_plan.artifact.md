# Implementation Plan - Non-navigating Loading Overlay and Error Handling

This plan outlines the changes to implement a non-navigating loading overlay and localized error handling in the authentication flow. The goal is to keep the user on the current screen during processing, showing a semi-transparent loading state, and displaying error messages directly on the screen if something goes wrong.

## Proposed Changes

### [UI Components]

#### [NEW] [LoadingOverlay.kt](file:///Users/hery/AndroidStudioProjects/SwaraBox/app/src/main/java/com/pamt/swarabox/ui/components/LoadingOverlay.kt)

- Create a `LoadingOverlay` component that displays a semi-transparent black background with a circular progress indicator.
- This overlay will NOT show errors; it will only indicate "Processing...".

### [Navigation & State Management]

#### [AppNavigation.kt](file:///Users/hery/AndroidStudioProjects/SwaraBox/app/src/main/java/com/pamt/swarabox/ui/AppNavigation.kt)

- Wrap `MainNavHost` in a `Box` to allow overlaying components.
- Logic to show `LoadingOverlay`:
    - Show if `AuthUiState` is `Loading`.
    - Show if `AuthCheckState` is `Authenticated` BUT `ProfileUiState` is `Loading`.
- Navigation Logic:
    - Only switch `startDestination` to `Home` if `ProfileUiState` is `Success`.
    - If `ProfileUiState` is `Error`, stay on the `Landing/Login` destination.

#### [LoginScreen.kt](file:///Users/hery/AndroidStudioProjects/SwaraBox/app/src/main/java/com/pamt/swarabox/ui/screens/LoginScreen.kt) & [RegisterEmailPasswordScreen.kt](file:///Users/hery/AndroidStudioProjects/SwaraBox/app/src/main/java/com/pamt/swarabox/ui/screens/RegisterEmailPasswordScreen.kt)

- Update these screens to accept an optional `errorMessage: String?`.
- Display the error message (e.g., in red text above the "Submit/Register" button) if it's not null.

## Verification Plan

### Manual Verification
1. **Login Loading**:
   - Enter credentials and click Login.
   - Verify: Screen stays on Login, dark overlay with loading animation appears.
2. **Login Error**:
   - Trigger an error (e.g., wrong password).
   - Verify: Overlay disappears, user stays on Login screen, and a red error message appears above the button.
3. **Registration Flow**:
   - Similar to Login, verify overlay during registration and error message display on failure.
4. **Profile Fetching**:
   - After successful Auth, verify the overlay stays visible while the profile is being fetched "behind the scenes" before the final transition to the Home screen.
