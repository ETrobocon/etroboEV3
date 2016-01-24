
using System;

using MonoBrickFirmware;
using MonoBrickFirmware.Display.Dialogs;
using MonoBrickFirmware.Display;
using MonoBrickFirmware.Native;


namespace AOTCompileApp
{
	class MainClass
	{
        // File Path
        static string monoPath = "/usr/local/bin/mono";
        static string ProgramPathSdCard = "/mnt/bootpar/apps";
        // Target Program
        static string TargetProgramFolder = "sample_c4";
        static string TargetProgramNameEXE = "sample_c4.exe";

        static void Main(string[] args)
        {
            try
            {
                // AOT Compile
                ProcessHelper.RunAndWaitForProcess(monoPath, "--aot=full " + ProgramPathSdCard + "/" + TargetProgramFolder + "/" + TargetProgramNameEXE);
                ProcessHelper.RunAndWaitForProcess(monoPath, "--aot=full " + ProgramPathSdCard + "/" + TargetProgramFolder + "/" + "MonoBrickFirmware.dll");
                var info = new InfoDialog("AOTCompileApp is Success.", true, "Program");
                info.Show();
            }
            catch
            {
                var info = new InfoDialog("AOTCompileApp is Error.", true, "Program");
                info.Show ();
            }
       }
	}
}

