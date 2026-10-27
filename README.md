# Tower War Bot — Android prototype

This is a starter Android Studio project for a user-controlled accessibility prototype.

## Current capabilities
- Opens Android Accessibility Settings so the user can explicitly enable the service.
- Sends one short test swipe to verify Android gesture access.

## Not implemented yet
- Screenshot capture and tower/color/number recognition.
- Strategy selection, automatic target selection, continuous play, or match restarting.
- A built APK; this environment does not have the Android SDK/Gradle installed to compile and test it.

## Build
Open this folder in Android Studio, let Gradle sync, then Build > Build APK(s). Install only on your own device. Enable the service in Android Settings > Accessibility > Installed apps/services. Android will show a system warning because accessibility can control gestures; enable it only if you understand and accept that permission.

## Next development step
Implement explicit screen capture with Android MediaProjection consent, then image processing to identify towers and determine source/target points. Validate on recorded gameplay before enabling automatic gestures. Game UI changes and device resolution can affect reliability.
