@echo off
setlocal enabledelayedexpansion

REM Paths relative to workspace
set APP_DIR=07-01-2026\springboot-mongo-crud
set IMAGE_NAME=omborse1618/springboot_crud_ops

REM Jenkins provides GIT_PREVIOUS_SUCCESSFUL_COMMIT; fallback to last commit if missing
for /f "delims=" %%i in ('git rev-parse HEAD') do set CURRENT_SHA=%%i

set PREV_SHA=%GIT_PREVIOUS_SUCCESSFUL_COMMIT%
if "%PREV_SHA%"=="" (
  echo No previous successful commit found; using previous commit on branch.
  for /f "delims=" %%i in ('git rev-parse HEAD^') do set PREV_SHA=%%i
)

echo Checking changes between %PREV_SHA% and %CURRENT_SHA% in %APP_DIR% ...
git diff --name-status %PREV_SHA% %CURRENT_SHA% -- "%APP_DIR%" > diff.txt
for /f %%A in ('findstr /r /c:".*" diff.txt ^| find /c /v ""') do set DIFF_COUNT=%%A

if "%DIFF_COUNT%"=="0" (
  echo No changes detected in %APP_DIR%. Skipping image build and push.
  exit /b 0
)

echo Changes detected: %DIFF_COUNT% file(s). Proceeding to build and push.

REM Build the Spring Boot jar
pushd "%APP_DIR%"
call gradlew.bat --no-daemon clean bootJar -x test
if errorlevel 1 (
  echo Gradle build failed.
  exit /b 1
)

REM Find the non-plain jar
for /f "delims=" %%j in ('dir /b build\libs ^| findstr /r /c:"\.jar" ^| findstr /v /c:"plain"') do set JAR_NAME=%%j
if "%JAR_NAME%"=="" (
  echo Jar not found in build\libs.
  exit /b 1
)

REM Build Docker image (context = app dir where Dockerfile is)
set TAG_BRANCH=main
set TAG_SHA=%CURRENT_SHA%

echo Building Docker image %IMAGE_NAME%:%TAG_SHA% ...
docker build -t %IMAGE_NAME%:%TAG_SHA% -t %IMAGE_NAME%:%TAG_BRANCH% -f Dockerfile .
if errorlevel 1 (
  echo Docker build failed.
  exit /b 1
)

echo Logging in to Docker Hub...
echo %DOCKERHUB_PASS% | docker login -u %DOCKERHUB_USER% --password-stdin
if errorlevel 1 (
  echo Docker login failed.
  exit /b 1
)

echo Pushing tags...
docker push %IMAGE_NAME%:%TAG_SHA%
if errorlevel 1 (
  echo Push (SHA tag) failed.
  exit /b 1
)
docker push %IMAGE_NAME%:%TAG_BRANCH%
if errorlevel 1 (
  echo Push (branch tag) failed.
  exit /b 1
)

popd
echo Done.
exit /b 0
