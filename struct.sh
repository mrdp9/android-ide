#!/bin/bash

PROJECT_NAME="android-tablet-ide"
PACKAGE_PATH="app/src/main/java/com/yourname/ide"

echo "🚀 Scaffolding Android Tablet IDE project: $PROJECT_NAME..."

# 1. Create the root directory and navigate into it
mkdir -p "$PROJECT_NAME"
cd "$PROJECT_NAME" || exit

# 2. Create the deep Android directory structure
mkdir -p "$PACKAGE_PATH"
mkdir -p "app/src/main/assets"
mkdir -p "app/src/main/res/values"
mkdir -p "app/src/main/res/drawable"
mkdir -p "app/src/main/res/mipmap-anydpi-v26"

# 3. Create root-level Gradle and Git files
touch build.gradle.kts
touch settings.gradle.kts
touch gradle.properties
touch gradlew
touch gradlew.bat
chmod +x gradlew # Make the Mac/Linux wrapper executable

# 4. Create app-level files
touch app/build.gradle.kts
touch app/src/main/AndroidManifest.xml

# 5. Create our specific hybrid IDE files
touch "app/src/main/assets/editor.html"
touch "$PACKAGE_PATH/MainActivity.kt"

# 6. Populate a basic README
cat <<EOF > README.md
# Android Tablet IDE 📱💻

A native Jetpack Compose Android application acting as an IDE, utilizing a WebView-bridge to run the Monaco Editor (the core of VS Code) specifically optimized for tablet screens.

## Architecture
- **UI:** Kotlin & Jetpack Compose
- **Editor:** Monaco (HTML/JS/CSS inside \`assets/\`)
EOF

# 7. Populate a standard Android .gitignore
cat <<EOF > .gitignore
*.iml
.gradle
/local.properties
/.idea/
.DS_Store
/build
/app/build
/captures
.externalNativeBuild
.cxx
EOF

# 8. Initialize a Git repository
git init

echo "✅ Project structure created successfully in the '$PROJECT_NAME' directory!"