# Pico Settings

Pico Settings is a tiny, unofficial launcher for Android system settings that
PICO OS does not normally expose in its own interface. It is intended for the
consumer PICO 4 and requests **no Android permissions**.

## Shortcuts

- Full Android Settings
- Private DNS
- Wi-Fi
- VPN
- Installed apps
- Developer options
- Display
- Device information

If PICO OS blocks or omits a specific panel, Pico Settings falls back to the
main Android Settings screen.

## Security properties

- No Internet permission
- No storage permission
- No accessibility service
- No VPN service
- No boot receiver or background service
- No analytics or external libraries
- No data is read, written, or transmitted

The complete application logic is contained in
`src/io/github/googleman81/picosettings/MainActivity.java`.

## Install

Enable Developer Mode and USB debugging on the headset, connect it to your
computer, and run:

```bash
adb install -r PicoSettings-v1.0.0.apk
```

The application should then appear under unknown/sideloaded applications.

To uninstall:

```bash
adb uninstall io.github.googleman81.picosettings
```

## Build

The project deliberately avoids Gradle and third-party dependencies. It uses
only JDK 17 and the standard Android SDK command-line tools.

Install Android platform 34 and build-tools 35.0.0, then provide a private
signing key through environment variables:

```bash
export ANDROID_SDK_ROOT=/path/to/android-sdk
export SIGNING_KEYSTORE=/private/path/pico-settings-release.p12
export SIGNING_ALIAS=pico-settings
export SIGNING_STORE_PASS='...'
export SIGNING_KEY_PASS='...'
./build.sh
```

The output is `build/PicoSettings-v1.0.0.apk`.

The GitHub Actions workflow compiles an **unsigned** APK to verify that the
published source builds. Release APKs should be signed outside GitHub unless
the repository owner deliberately configures protected signing secrets.

## Compatibility

The system intents target standard Android 10+ settings panels. Initial source
construction and build verification were performed independently; compatibility
with a particular PICO OS release must be confirmed on real hardware.

Pico Settings is not affiliated with or endorsed by PICO Immersive or ByteDance.

## License

MIT
