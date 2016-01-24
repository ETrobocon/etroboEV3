@echo off
if "%OS%" == "Windows_NT" goto :winnt

:win98
	echo Windows 9x/ME is no longer supported.
	echo Please upgrade to Windows 2000 or later.
	goto :eof

:append_jar
	set "TMP_CP=%TMP_CP%;%TMP_JAR%"
	goto :eof

:build_classpath
	if not exist "%~2" (
		echo Internal error. The following directory does not exist:
		echo   "%~2"
		exit /B 1
	)

	set "TMP_CP="
	for /R "%~2" %%i in (*.jar) do (
		set "TMP_JAR=%%i"
		call :append_jar
	)
	set "%~1=%TMP_CP:~1%"
	goto :eof

:search_path
	set "%~1=%~f$PATH:2"
	goto :eof

:find_java_and_javac
	call :search_path JAVA java.exe
	call :search_path JAVAC javac.exe
	if "%JAVA%" == "" (
		echo java.exe was not found in the default search path.
		echo Install a JDK and set the variable LEJOS_EV3_JAVA_HOME
		echo to the root directory of the JDK.
		exit /B 1
	) else if "%JAVAC%" == "" (
		echo javac.exe was not found in the default search path.
		echo Consider setting the variable LEJOS_EV3_JAVA_HOME to
		echo the root directory of a JDK. Otherwise,
		echo some tools might not work.
	)
	goto :eof

:set_java_and_javac
	set "JAVA=%~2\bin\java.exe"
	set "JAVAC=%~2\bin\javac.exe"
	if not exist "%JAVA%" (
		echo The variable %~1 does not point to the root directory of
		echo a JRE or JDK. The following executable does not exist:
		echo   "%JAVA%" 
		exit /B 1
	) else if not exist "%JAVAC%" (
		echo The variable %~1 seems to point to the root directory of
		echo a JRE. It should point to the root directory of a JDK.
		echo Otherwise, some tools might not work.
	)
	goto :eof

:winnt
	setlocal
	set "EV3_COMMAND=%~n0"
	set "EV3_HOME=%~dp0\.."

	call :build_classpath EV3_CP_PC "%%EV3_HOME%%\lib"

	if not "%LEJOS_EV3_JAVA_HOME%" == "" (
		call :set_java_and_javac LEJOS_EV3_JAVA_HOME "%%LEJOS_EV3_JAVA_HOME%%" 
	) else if not "%JAVA_HOME%" == "" (
		call :set_java_and_javac JAVA_HOME "%%JAVA_HOME%%" 
	) else (
		call :find_java_and_javac
	)


"%JAVA%" "-DEV3.home=%EV3_HOME%" "-DCOMMAND_NAME=%EV3_COMMAND%" -classpath "%EV3_CP_PC%" jp.etrobo.ev3.tools.BluetoothSetting %*
:eof
