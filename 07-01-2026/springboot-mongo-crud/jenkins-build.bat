@echo off
setlocal enabledelayedexpansion

REM ── Resolve to the script's directory (the project root) ─────────────────────
pushd "%~dp0"

REM ── Settings ─────────────────────────────────────────────────────────────────
set "IMAGE_NAME=omborse1618/springboot_crud_ops"

REM Try to infer branch tag from Jenkins; fallback to main
set "TAG_BRANCH=%GIT_BRANCH%"
if "%TAG_BRANCH%"=="" set "TAG_BRANCH=main"
set "TAG_BRANCH=%TAG_BRANCH:origin/=%"
set "TAG_BRANCH=%TAG_BRANCH:refs/heads/=%"

REM ── Identify commit range ────────────────────────────────────────────────────
for /f "delims=" %%i in ('git rev-parse HEAD') do set "CURRENT_SHA=%%i"

set "PREV_SHA=%GIT_PREVIOUS_SUCCESSFUL_COMMIT%"
if "%PREV_SHA%"=="" set "PREV_SHA=%GIT_PREVIOUS_COMMIT%"

if "%PREV_SHA%"=="" (
  REM Fallback: take the previous commit if it exists
  for /f "skip=1 delims=" %%i in ('git rev-list --max-count=2 HEAD') do set "PREV_SHA=%%i"
)

echo Current: %CURRENT_SHA%
if not "%PREV_SHA%"=="" (echo Previous: %PREV_SHA%) else (echo Previous: \(none\) \- first run will build)

REM ── Detect changes in this project directory only ────────────────────────────
set "DIFF_FILE=%TEMP%\jenkins_diff_%RANDOM%.txt"
set "CHANGES_FOUND=0"

if "%PREV_SHA%"=="" (
  REM No previous commit to diff against \-\- treat as changed
  set "CHANGES_FOUND=1"
) else (
  git diff --name-status --diff-filter=ACDMR "%PREV_SHA%" "%CURRENT_SHA%" -- . > "%DIFF_FILE%"
  set /a DIFF_COUNT=0
  for /f "usebackq delims=" %%L in ("%DIFF_FILE%") do set /a DIFF_COUNT+=1

  if %DIFF_COUNT% GTR 0 (
    set "CHANGES_FOUND=1"
    echo Changes detected in project: %DIFF_COUNT% file\(s\)
    type "%DIFF_FILE%"
  ) else (
    echo No changes detected in this project folder. Skipping build and push.
    del /q "%DIFF_FILE%" 2>nul
    popd
    exit /b 0
  )
)

del /q "%DIFF_FILE%" 2>nul

REM ── Build Spring Boot JAR ────────────────────────────────────────────────────
echo Building application JAR...
if exist gradlew.bat (
  call gradlew.bat --no-daemon clean bootJar -x test
) else (
  echo gradlew.bat not found in script directory. >&2
  popd
  exit /b 1
)
if errorlevel 1 (
  echo Gradle build failed. >&2
  popd
  exit /b 1
)

REM Pick the non\-plain jar
set "JAR_NAME="
for /f "delims=" %%j in ('dir /b build\libs ^| findstr /i /r "\.jar$" ^| findstr /i /v "plain"') do set "JAR_NAME=%%j"
if "%JAR_NAME%"=="" (
  echo Jar not found in `build\libs`. >&2
  popd
  exit /b 1
)

REM ── Build and push Docker image ──────────────────────────────────────────────
echo Building Docker image %IMAGE_NAME%:%CURRENT_SHA% and %IMAGE_NAME%:%TAG_BRANCH% ...
if exist Dockerfile (
  docker build -t %IMAGE_NAME%:%CURRENT_SHA% -t %IMAGE_NAME%:%TAG_BRANCH% -f Dockerfile .
) else (
  echo Dockerfile not found in script directory. >&2
  popd
  exit /b 1
)
if errorlevel 1 (
  echo Docker build failed. >&2
  popd
  exit /b 1
)

echo Logging in to Docker Hub...
if "%DOCKERHUB_USER%"=="" (
  echo DOCKERHUB_USER is not set. Configure Jenkins credentials to env vars `DOCKERHUB_USER` and `DOCKERHUB_PASS`. >&2
  popd
  exit /b 1
)
if "%DOCKERHUB_PASS%"=="" (
  echo DOCKERHUB_PASS is not set. Configure Jenkins credentials to env vars `DOCKERHUB_USER` and `DOCKERHUB_PASS`. >&2
  popd
  exit /b 1
)

echo %DOCKERHUB_PASS% | docker login -u %DOCKERHUB_USER% --password-stdin
if errorlevel 1 (
  echo Docker login failed. >&2
  popd
  exit /b 1
)

echo Pushing images...
docker push %IMAGE_NAME%:%CURRENT_SHA%
if errorlevel 1 (
  echo Push failed for SHA tag. >&2
  popd
  exit /b 1
)
docker push %IMAGE_NAME%:%TAG_BRANCH%
if errorlevel 1 (
  echo Push failed for branch tag. >&2
  popd
  exit /b 1
)

echo Done.
popd
exit /b 0
