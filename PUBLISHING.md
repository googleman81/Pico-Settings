# Publishing on GitHub without sharing credentials

The repository source, release APK, and signing key are separate artifacts.
Only the source and release APK belong on GitHub. Never upload the `.p12`
signing key or its password file.

## 1. Create the repository

On GitHub, create a new empty public repository named `PicoSettings`. Do not
ask GitHub to add a README, license, or `.gitignore`; those are already here.

## 2. Push the source from your Mac

Unzip the source archive, open Terminal in the extracted `PicoSettings`
directory, and run:

```bash
git init
git add .
git commit -m "Initial Pico Settings release"
git branch -M main
git remote add origin https://github.com/googleman81/PicoSettings.git
git push -u origin main
```

Authentication happens between your Mac and GitHub. No GitHub password or
token needs to be given to anyone else.

## 3. Publish the APK

On the repository page:

1. Open **Releases** and select **Draft a new release**.
2. Create the tag `v1.0.0` from `main`.
3. Set the title to `Pico Settings 1.0.0`.
4. Attach `PicoSettings-v1.0.0.apk` and `SHA256SUMS.txt`.
5. State that this is the first hardware-test release and publish it.

The Actions tab will also compile an unsigned APK from the public source. That
is a reproducibility check, not the signed installable release.

## Future releases

Every APK using package name `io.github.googleman81.picosettings` must be
signed with the same private key to install as an update. Keep the private key
and password backup offline. If the key is lost, users must uninstall the old
version before installing a replacement signed by a new key.
