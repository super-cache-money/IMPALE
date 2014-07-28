setlocal
set javaroot="notfound"
if exist "C:\Program Files (x86)\Java\" (
set "javaroot=C:\Program Files (x86)\Java\"
goto ENDOFIF
)
if exist "C:\Program Files\Java\" (
set "javaroot=C:\Program Files\Java\"
goto ENDOFIF
) 
:ENDOFIF
if "%javaroot%" == "notfound" (
goto END
)
pushd "%javaroot%"
for /f "tokens=*" %%a in ('dir /b /od') do set newest=%%a
set versiondir=%newest%
popd
echo %javaroot%%versiondir%\bin\> config\javapath.txt
echo Java Path Setting Complete...
