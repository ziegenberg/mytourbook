[Setup]
AppName=MyTourbook
AppVerName=MyTourbook 0.8.9
AppCopyright=MyTourbook Contributors 2005, 2007
AppID={{37852811-BC7D-411C-8122-E69CCA892582}

LicenseFile=M:\mytourbook-workspace-TRUNK\net.tourbook\gpl.txt

OutputDir=m:\MyTourbook Product\0.8.9
OutputBaseFilename=mytourbook_0.8.9.win32_setup

DefaultGroupName=MyTourbook
DefaultDirName={pf}\MyTourbook

VersionInfoVersion=0.8.9
VersionInfoCompany=MyTourbook

WizardImageBackColor=clWhite
WizardSmallImageFile=m:\mytourbook-workspace-TRUNK\net.tourbook\icons\tourbook48-32-white.bmp

LanguageDetectionMethod=locale

[Files]
Source: m:\MyTourbook Product\0.8.9\mytourbook_0.8.9.win32.win32.x86\mytourbook\*; DestDir: {app}; Flags: recursesubdirs; Tasks: ; Languages:

[InstallDelete]
Name: {app}\*; Type: files
Name: {app}\configuration; Type: filesandordirs
Name: {app}\features; Type: filesandordirs
Name: {app}\plugins; Type: filesandordirs
Name: {app}\workspace; Type: filesandordirs
Name: {app}; Type: dirifempty
Name: {userappdata}\..\mytourbook\org.eclipse.osgi\splash.bmp; Type: files; Tasks: ; Languages: 

[Icons]
Name: {group}\MyTourbook; Filename: {app}\mytourbook.exe; IconFilename: {app}\mytourbook.exe
Name: {group}\Uninstall MyTourbook; Filename: {app}\{uninstallexe}; IconFilename: {uninstallexe}
Name: {commondesktop}\MyTourbook; Filename: {app}\mytourbook.exe; IconFilename: {app}\mytourbook.exe; WorkingDir: {app}; Tasks: desktopicon\common
Name: {userdesktop}\MyTourbook; Filename: {app}\mytourbook.exe; IconFilename: {app}\mytourbook.exe; WorkingDir: {app}; Tasks: desktopicon\user
Name: {userappdata}\Microsoft\Internet Explorer\Quick Launch\MyTourbook; Filename: {app}\MyTourbook.exe; WorkingDir: {app}; Tasks: " quicklaunchicon"

[Tasks]
Name: desktopicon; Description: {cm:CreateDesktopIcon}; GroupDescription: {cm:GroupCreateIcons}
Name: desktopicon\common; Description: {cm:AllUsers}; GroupDescription: {cm:GroupCreateIcons}; Flags: exclusive
Name: desktopicon\user; Description: {cm:CurrentUser}; GroupDescription: {cm:GroupCreateIcons}; Flags: exclusive unchecked
Name: quicklaunchicon; Description: {cm:CreateQuickLaunchIcon}


[Run]
Filename: {app}\mytourbook.exe; WorkingDir: {app}; Description: {cm:StartMyTourbook}; Flags: postinstall nowait

[Languages]
Name: de; MessagesFile: compiler:Languages\German.isl; LicenseFile: M:\mytourbook-workspace-TRUNK\net.tourbook\gpl-de.rtf
Name: en; MessagesFile: compiler:Default.isl

[CustomMessages]
en.GroupCreateIcons=Additional icons:
en.CreateDesktopIcon=Create a &desktop icon
en.AllUsers=For &all users
en.CurrentUser=For the &current user only

de.GroupCreateIcons=Icons erstellen:
de.CreateDesktopIcon=Symbol auf dem &Desktop erstellen
de.AllUsers=F�r &alle Benutzer
de.CurrentUser=Nur f�r den aktuellen &Benutzer

en.CreateQuickLaunchIcon=Create a &quick launch icon
de.CreateQuickLaunchIcon=&Schnellstart Symbol erstellen

en.StartMyTourbook=Run &MyTourbook
de.StartMyTourbook=&MyTourbook starten
