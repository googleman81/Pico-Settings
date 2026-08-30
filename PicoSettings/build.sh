#!/usr/bin/env bash
set -euo pipefail

project_dir="$(cd "$(dirname "$0")" && pwd)"
sdk_root="${ANDROID_SDK_ROOT:-${ANDROID_HOME:-}}"
build_tools_version="${BUILD_TOOLS_VERSION:-35.0.0}"
platform_version="${PLATFORM_VERSION:-34}"
build_dir="$project_dir/build"
compiled_dir="$build_dir/compiled"
classes_dir="$build_dir/classes"
generated_dir="$build_dir/generated"
dex_dir="$build_dir/dex"
classes_jar="$build_dir/classes.jar"
unsigned_apk="$build_dir/PicoSettings-unsigned.apk"
aligned_apk="$build_dir/PicoSettings-aligned.apk"
output_apk="$build_dir/PicoSettings-v1.0.0.apk"

if [[ -z "$sdk_root" ]]; then
    echo "Set ANDROID_SDK_ROOT (or ANDROID_HOME) to your Android SDK directory." >&2
    exit 1
fi

build_tools="$sdk_root/build-tools/$build_tools_version"
android_jar="$sdk_root/platforms/android-$platform_version/android.jar"

for tool in aapt2 d8 zipalign apksigner; do
    if [[ ! -x "$build_tools/$tool" ]]; then
        echo "Missing $build_tools/$tool" >&2
        exit 1
    fi
done

if [[ ! -f "$android_jar" ]]; then
    echo "Missing $android_jar" >&2
    exit 1
fi

mkdir -p "$compiled_dir" "$classes_dir" "$generated_dir" "$dex_dir"
find "$compiled_dir" "$classes_dir" "$generated_dir" "$dex_dir" -mindepth 1 -delete

"$build_tools/aapt2" compile --dir "$project_dir/res" -o "$compiled_dir"
"$build_tools/aapt2" link \
    -I "$android_jar" \
    --manifest "$project_dir/AndroidManifest.xml" \
    --java "$generated_dir" \
    -o "$unsigned_apk" \
    "$compiled_dir"/*.flat

javac --release 8 \
    -classpath "$android_jar" \
    -d "$classes_dir" \
    "$generated_dir/io/github/googleman81/picosettings/R.java" \
    "$project_dir/src/io/github/googleman81/picosettings/MainActivity.java"

(cd "$classes_dir" && jar cf "$classes_jar" .)
"$build_tools/d8" --lib "$android_jar" --output "$dex_dir" "$classes_jar"
(cd "$dex_dir" && zip -q -j "$unsigned_apk" classes.dex)
"$build_tools/zipalign" -f 4 "$unsigned_apk" "$aligned_apk"

keystore="${SIGNING_KEYSTORE:-$project_dir/signing/pico-settings-release.p12}"
key_alias="${SIGNING_ALIAS:-pico-settings}"

if [[ ! -f "$keystore" ]]; then
    echo "Missing signing keystore: $keystore" >&2
    echo "Set SIGNING_KEYSTORE, SIGNING_ALIAS, SIGNING_STORE_PASS and SIGNING_KEY_PASS." >&2
    exit 1
fi

: "${SIGNING_STORE_PASS:?SIGNING_STORE_PASS is required}"
: "${SIGNING_KEY_PASS:?SIGNING_KEY_PASS is required}"

"$build_tools/apksigner" sign \
    --ks "$keystore" \
    --ks-key-alias "$key_alias" \
    --ks-pass "env:SIGNING_STORE_PASS" \
    --key-pass "env:SIGNING_KEY_PASS" \
    --out "$output_apk" \
    "$aligned_apk"

"$build_tools/apksigner" verify --verbose --print-certs "$output_apk"
echo "Built: $output_apk"
