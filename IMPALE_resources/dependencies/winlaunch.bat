setlocal
SET oldpath=%PATH%
java -version >nul 2>&1 && (
break > config\javapath.txt
GOTO Launch
) || (
GOTO UsePathSetter    
)

:UsePathSetter
echo Using Java Path Setter
set OLDDIR=%CD%
call "%CD%\dependencies\pathsetter.bat"
set /p jpath=<config\javapath.txt
SET PATH=%jpath%
GOTO Launch

:Launch
echo Launching IMPALE
set OLDDIR=%CD%
set /p jpath=<config\javapath.txt
for /f tokens^=2-5^ delims^=.-_^" %%j in ('java -fullversion 2^>^&1') do set "jver=%%j%%k"
java -version >nul 2>&1 && (
echo java found
) || (
%windir%\System32\mshta.exe "javascript:alert('Java could not be found on your system.\n\nPlease download it from www.java.com');close();"
GOTO End
)
if %jver% lss 17 (
%windir%\System32\mshta.exe "javascript:alert('Your version of java is too old.\n\nPlease download the latest version from www.java.com');close();"
GOTO End
)
SET PATH=%oldpath% 
"%jpath%java" -XX:+AggressiveHeap -jar "%OLDDIR%/dependencies/tinytest.jar" >nul 2>&1 && (
2>nul (
  >>/temp/output.log echo off
) && (
"%jpath%java" -XX:+AggressiveHeap -jar "%OLDDIR%/internals.jar" >%OLDDIR%/temp/output.log 2>%OLDDIR%/temp/errors.log
) || (
"%jpath%java" -XX:+AggressiveHeap -jar "%OLDDIR%/internals.jar"
)
) || (
mshta "javascript:alert('IMPALE was not able to use as much memory as it should.\nThe low-memory version will now be run.\n\nHave less applications running before launching IMPALE next time!');close();"
2>nul (
  >>/temp/output.log echo off
) && (
"%jpath%java" -jar "%OLDDIR%/internals.jar" >%OLDDIR%/temp/output.log 2>%OLDDIR%/temp/errors.log
) || (
"%jpath%java" -jar "%OLDDIR%/internals.jar"
)
)

GOTO End



:End
