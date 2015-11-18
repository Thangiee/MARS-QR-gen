@ECHO OFF

setlocal ENABLEEXTENSIONS

IF %PROCESSOR_ARCHITECTURE%==AMD64 GOTO CHECK_JAVA_64
IF %PROCESSOR_ARCHITECTURE%==x86 GOTO CHECK_JAVA_32

@ECHO Detecting Java version installed.
:CHECK_JAVA_64
@ECHO Detecting if it is 64 bit machine
set KEY_NAME="HKEY_LOCAL_MACHINE\Software\Wow6432Node\JavaSoft\Java Runtime Environment"
set VALUE_NAME=CurrentVersion
set VALUE_FAMILYNAME=Java6FamilyVersion

FOR /F "usebackq skip=2 tokens=1-3" %%A IN (`REG QUERY %KEY_NAME% /v %VALUE_NAME% 2^>nul`) DO (
    set ValueName=%%A
    set ValueType=%%B
    set ValueValue=%%C
)
@ECHO CurrentVersion %ValueValue%

IF "%ValueValue%" == "1.6" (
    FOR /F "usebackq skip=2 tokens=1-3" %%A IN (`REG QUERY %KEY_NAME% /v %VALUE_FAMILYNAME% 2^>nul`) DO (
        set ValueFamilyName=%%A
        set ValueFamilyType=%%B
        set ValueFamilyValue=%%C
    )
)
IF "%ValueValue%" == "1.6" (
    IF "%ValueFamilyValue%" LSS "1.6.0_16" GOTO JAVA_NOT_INSTALLED
)

SET KEY_NAME="%KEY_NAME:~1,-1%\%ValueValue%"
SET VALUE_NAME=JavaHome

if defined ValueName (
    FOR /F "usebackq skip=2 tokens=1,2*" %%A IN (`REG QUERY %KEY_NAME% /v %VALUE_NAME% 2^>nul`) DO (
        set ValueName2=%%A
        set ValueType2=%%B
        set JRE_PATH2=%%C

        if defined ValueName2 (
            set ValueName = %ValueName2%
            set ValueType = %ValueType2%
            set ValueValue =  %JRE_PATH2%
        )
    )
)

IF NOT "%JRE_PATH2%" == "" GOTO JAVA_INSTALLED
IF "%JRE_PATH2%" == "" GOTO JAVA_NOT_INSTALLED

:CHECK_JAVA_32
@ECHO Detecting if it is 32 bit machine
set KEY_NAME="HKEY_LOCAL_MACHINE\Software\JavaSoft\Java Runtime Environment"
set VALUE_NAME=CurrentVersion
set VALUE_FAMILYNAME=Java6FamilyVersion

FOR /F "usebackq skip=2 tokens=1-3" %%A IN (`REG QUERY %KEY_NAME% /v %VALUE_NAME% 2^>nul`) DO (
    set ValueName=%%A
    set ValueType=%%B
    set ValueValue=%%C
)
@ECHO CurrentVersion %ValueValue%
IF "%ValueValue%" == "" GOTO CHECK_JAVA_32

IF "%ValueValue%" == "1.6" (
    FOR /F "usebackq skip=2 tokens=1-3" %%A IN (`REG QUERY %KEY_NAME% /v %VALUE_FAMILYNAME% 2^>nul`) DO (
        set ValueFamilyName=%%A
        set ValueFamilyType=%%B
        set ValueFamilyValue=%%C
    )
)
IF "%ValueValue%" == "1.6" (
    IF "%ValueFamilyValue%" LSS "1.6.0_16" GOTO JAVA_NOT_INSTALLED
)

SET KEY_NAME="%KEY_NAME:~1,-1%\%ValueValue%"
SET VALUE_NAME=JavaHome

if defined ValueName (
    FOR /F "usebackq skip=2 tokens=1,2*" %%A IN (`REG QUERY %KEY_NAME% /v %VALUE_NAME% 2^>nul`) DO (
        set ValueName2=%%A
        set ValueType2=%%B
        set JRE_PATH2=%%C

        if defined ValueName2 (
            set ValueName = %ValueName2%
            set ValueType = %ValueType2%
            set ValueValue =  %JRE_PATH2%
        )
    )
)

IF "%JRE_PATH2%" == ""  GOTO JAVA_NOT_INSTALLED

:JAVA_INSTALLED
SET JAVAW=%JRE_PATH2%\bin\javaw
start "" "%JAVAW%" -jar QR-gen-gui.jar
exit
GOTO END

:JAVA_NOT_INSTALLED
ECHO ERROR: Java not found. Please make sure java is installed and 
ECHO validate that the java bin directory is in your the environment variable. 
ECHO.
ECHO For help on setting the environment variable, see: 
ECHO https://docs.oracle.com/javase/tutorial/essential/environment/paths.html
ECHO.
PAUSE
GOTO END

:END
REM End of Batch File