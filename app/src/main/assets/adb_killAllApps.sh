#!/bin/sh



# timeCam

echo  ### "### install apps"

adb install /home/kollrabi/Downloads/org.fossify.gallery_30.apk
#adb install /home/kollrabi/Downloads/com.iris.gallery_17.apk
#adb install /home/kollrabi/Downloads/com.amaze.filemanager_126.apk
adb install /home/kollrabi/Downloads/org.fossify.filemanager_15.apk

# time laps
adb install /home/kollrabi/AndroidStudioProjects/TimeCam/app/build/outputs/apk/release/app-release.apk
#adb install /home/kollrabi/AndroidStudioProjects/TimeCam/app/build/outputs/apk/debug/app-debug.apk

# jiggle
adb install /home/kollrabi/AndroidStudioProjects/DoNotJiggle/app/build/outputs/apk/debug/app-debug.apk

echo  ### "### google android"

#adb shell pm uninstall --user 0 com.google.vr.vrcore

adb shell pm uninstall --user 0 com.google.android.apps.docs
adb shell pm uninstall --user 0 com.google.android.apps.tachyon
adb shell pm uninstall --user 0 com.google.android.videos
adb shell pm uninstall --user 0 com.google.android.music
adb shell pm uninstall --user 0 com.google.android.talk
adb shell pm uninstall --user 0 com.google.android.apps.photos
adb shell pm uninstall --user 0 com.google.android.gm
adb shell pm uninstall --user 0 com.google.android.youtube
adb shell pm uninstall --user 0 com.google.android.apps.maps
adb shell pm uninstall --user 0 com.google.android.backuptransport
adb shell pm uninstall --user 0 com.google.android.googlequicksearchbox
adb shell pm uninstall --user 0 com.google.android.partnersetup
adb shell pm uninstall --user 0 com.google.android.syncadapters.contacts
adb shell pm uninstall --user 0 com.google.android.syncadapters.calendar
adb shell pm uninstall --user 0 com.google.android.calendar
adb shell pm uninstall --user 0 com.google.android.marvin.talkback
#adb shell pm uninstall --user 0 com.google.android.onetimeinitializer
#adb shell pm uninstall --user 0 com.google.android.gms
#adb shell pm uninstall --user 0 com.google.android.gsf
adb shell pm uninstall --user 0 com.google.android.tts
adb shell pm uninstall --user 0 com.google.android.feedback
adb shell pm uninstall --user 0 com.google.android.printservice.recommendation
adb shell pm uninstall --user 0 com.google.android.apps.docs.editors.sheets
adb shell pm uninstall --user 0 com.google.android.apps.docs.editors.slides
adb shell pm uninstall --user 0 com.google.android.apps.docs.editors.docs

echo  ### "### android"

#### folgende NICHT loeschen!!!
#adb shell pm uninstall --user 0 com.android.settings
#adb shell pm uninstall --user 0 com.android.providers.downloads.ui
#adb shell pm uninstall --user 0 com.android.providers.contacts
#adb shell pm uninstall --user 0 com.android.providers.blockednumber
#adb shell pm uninstall --user 0 com.android.providers.userdictionary
#adb shell pm uninstall --user 0 com.android.providers.calendar
#adb shell pm uninstall --user 0 com.android.providers.telephony
#adb shell pm uninstall --user 0 com.android.providers.media # adb!!!

#adb shell pm uninstall --user 0 com.google.android.overlay.gmsconfig
#adb shell pm uninstall --user 0 com.google.android.overlay.managedprovisioning
#adb shell pm uninstall --user 0 com.google.android.overlay.settingsProvider

adb shell pm uninstall --user 0 com.android.mediacenter # music
adb shell pm uninstall --user 0 com.android.calculator2
adb shell pm uninstall --user 0 com.android.camera2
adb shell pm uninstall --user 0 com.android.gallery3d
adb shell pm uninstall --user 0 com.android.calendar
adb shell pm uninstall --user 0 com.android.email
adb shell pm uninstall --user 0 com.android.exchange
adb shell pm uninstall --user 0 com.android.chrome
adb shell pm uninstall --user 0 org.android.messaging
adb shell pm uninstall --user 0 com.android.phone
adb shell pm uninstall --user 0 com.android.contacts
adb shell pm uninstall --user 0 com.android.deskclock
adb shell pm uninstall --user 0 com.android.wallpaperbackup
adb shell pm uninstall --user 0 com.android.emergency

adb shell pm uninstall --user 0 com.android.wallpapercropper
adb shell pm uninstall --user 0 com.android.server.telecom
#adb shell pm uninstall --user 0 com.android.nfc
adb shell pm uninstall --user 0 com.android.mms.service
adb shell pm uninstall --user 0 com.android.mms
adb shell pm uninstall --user 0 com.android.soundrecorder
adb shell pm uninstall --user 0 com.android.egg
adb shell pm uninstall --user 0 com.android.wallpaper.livepicker
adb shell pm uninstall --user 0 com.android.bookmarkprovider
adb shell pm uninstall --user 0 com.android.bips # Built-in Print Service
adb shell pm uninstall --user 0 com.android.printspooler
adb shell pm uninstall --user 0 com.android.chrome
adb shell pm uninstall --user 0 com.android.egg
#adb shell pm uninstall --user 0 com.android.vending # Google Play Store downloads
#adb shell pm uninstall --user 0 com.android.documentsui
#adb shell pm uninstall --user 0 com.android.shell
#adb shell pm uninstall --user 0 com.android.storagemanager
#adb shell pm uninstall --user 0 com.android.htmlviewer

adb shell pm uninstall --user 0 com.sec.android.gallery3d
adb shell pm uninstall --user 0 com.sec.android.app.myfiles
adb shell pm uninstall --user 0 com.sec.phone
adb shell pm uninstall --user 0 com.sec.android.app.camera
adb shell pm uninstall --user 0 com.sec.android.camera2
adb shell pm uninstall --user 0 com.sec.android.app.clockpackage
#adb shell pm uninstall --user 0 com.sec.android.cover.ledcover
adb shell pm uninstall --user 0 com.sec.android.app.sbrowser # samsung browser
adb shell pm uninstall --user 0 com.sec.android.app.samsungapps
adb shell pm uninstall --user 0 com.sec.android.widgetapp.samsungapps
adb shell pm uninstall --user 0 com.sec.android.app.shealth # Samsungs Gesundheits- und Fitness-App
adb shell pm uninstall --user 0 com.sec.android.app.popupcalculator
adb shell pm uninstall --user 0 com.sec.android.app.voicenote
adb shell pm uninstall --user 0 com.sec.android.widgetapp.easymodecontactswidget
adb shell pm uninstall --user 0 com.sec.ims # Samsungs IMS-Telefoniedienste. IMS (IP Multimedia Subsystem)
adb shell pm uninstall --user 0 com.sec.imslogger
adb shell pm uninstall --user 0 com.sec.imsservice
adb shell pm uninstall --user 0 com.sec.vsimservice # samsung vSIM-bezogene Mobilfunkfunktionen.
#adb shell pm uninstall --user 0 com.sec.android.sdhms # Samsung Device Health Manager Service, Überwachung des Gerätezustands.
adb shell pm uninstall --user 0 com.sec.android.app.fm # radio
adb shell pm uninstall --user 0 com.sec.spp.push
adb shell pm uninstall --user 0 com.sec.android.mimage.photoretouching
adb shell pm uninstall --user 0 com.sec.android.easyMover.Agent


echo  ### "### externe apps"

adb shell pm uninstall --user 0 com.instagram.android
adb shell pm uninstall --user 0 com.linkedin.android
adb shell pm uninstall --user 0 com.spotify.music
adb shell pm uninstall --user 0 com.whatsapp
adb shell pm uninstall --user 0 com.skype.raider
adb shell pm uninstall --user 0 com.twitter.android
adb shell pm uninstall --user 0 com.facebook.katana
#adb shell pm uninstall --user 0 com.facebook.apadb shell pmanager
adb shell pm uninstall --user 0 com.facebook.system
adb shell pm uninstall --user 0 com.facebook.services
adb shell pm uninstall --user 0 com.facebook.appmanager
adb shell pm uninstall --user 0 com.osp.app.signin #Anmeldung beim Samsung-Konto
adb shell pm uninstall --user 0 com.hicloud.android.clone
adb shell pm uninstall --user 0 com.hisi.mapcon
adb shell pm uninstall --user 0 com.hp.android.printservice
adb shell pm uninstall --user 0 com.qeexo.smartshot
adb shell pm uninstall --user 0 com.example.android.notepad
adb shell pm uninstall --user 0 com.enhance.gameservice

echo  ### "### microsoft"

adb shell pm uninstall --user 0 com.microsoft.office.excel
adb shell pm uninstall --user 0 com.microsoft.office.word
adb shell pm uninstall --user 0 com.microsoft.office.powerpoint
adb shell pm uninstall --user 0 com.microsoft.office.officehubrow
adb shell pm uninstall --user 0 com.microsoft.skydrive

echo  ### "### games"

adb shell pm uninstall --user 0 com.gameloft.android.GloftANPH
adb shell pm uninstall --user 0 com.gameloft.android.GloftDMKF
adb shell pm uninstall --user 0 com.gameloft.android.GloftSCCA
adb shell pm uninstall --user 0 com.gameloft.android.GloftSMIF

echo  ### "### huawei radikal"

#adb shell pm uninstall --user 0 com.huawei.systemmanager # !!!
#adb shell pm uninstall --user 0 com.huawei.systemserver # !!!

adb shell pm uninstall --user 0 com.huawei.android.chr # HwChrService erfasst Nutzungs- und Diagnoseinformationen
adb shell pm uninstall --user 0 com.huawei.gameassistant
adb shell pm uninstall --user 0 com.huawei.scanner
adb shell pm uninstall --user 0 com.huawei.android.tips
adb shell pm uninstall --user 0 com.huawei.tips
adb shell pm uninstall --user 0 com.huawei.appmarket
adb shell pm uninstall --user 0 com.huawei.phoneservice
adb shell pm uninstall --user 0 com.huawei.android.thememanager
adb shell pm uninstall --user 0 com.huawei.nearby
adb shell pm uninstall --user 0 com.huawei.HwMultiScreenShot
adb shell pm uninstall --user 0 com.huawei.android.FloatTasks
adb shell pm uninstall --user 0 com.huawei.intelligent
adb shell pm uninstall --user 0 com.huawei.motionservice
adb shell pm uninstall --user 0 com.huawei.android.FMRadio
adb shell pm uninstall --user 0 com.huawei.android.totemweather
adb shell pm uninstall --user 0 com.huawei.android.totemweatherwidget
adb shell pm uninstall --user 0 com.huawei.android.widget
adb shell pm uninstall --user 0 com.huawei.gameassistant
adb shell pm uninstall --user 0 com.huawei.synergy # scheint Huaweis geräte- und netzwerkübergreifende Synergie-/Verbindungsfunktionen zu unterstützen
adb shell pm uninstall --user 0 com.huawei.compass
adb shell pm uninstall --user 0 com.huawei.android.totemweatherapp
adb shell pm uninstall --user 0 com.huawei.android.dsdscardmanager
#adb shell pm uninstall --user 0 com.huawei.android.hwaps # verwaltet Energiesparen und Hintergrund-Apps und kann Apps im Hintergrund beenden
adb shell pm uninstall --user 0 com.huawei.android.hwupgradeguide
adb shell pm uninstall --user 0 com.huawei.android.projectmenu
adb shell pm uninstall --user 0 com.huawei.behaviorauth # Name deutet auf eine Huawei-interne Authentifizierungs- oder Verhaltensdienst-Komponente hin
adb shell pm uninstall --user 0 com.huawei.camera
#adb shell pm uninstall --user 0 com.huawei.iconnect # in Huawei-Systemdienst zur Verbindung und Kommunikation mit Geräten
adb shell pm uninstall --user 0 com.huawei.imedia.sws # Audioeffekte wie 3D-/Surround-Klang
adb shell pm uninstall --user 0 com.huawei.hwasm # Schnittstelle zwischen kompatiblen Apps und dem Authentifizierungsverfahren
adb shell pm uninstall --user 0 com.huawei.android.hwouc # Firmware-Updates und ermöglicht deren Download und Installation
adb shell pm uninstall --user 0 com.huawei.android.wfdft #  Huawei Share und unterstützt die Dateiübertragung zwischen Geräten über Wi‑Fi Direct
adb shell pm uninstall --user 0 com.huawei.bd # Huaweis HwUE-App („Huawei User Experience“)
adb shell pm uninstall --user 0 com.huawei.indexsearch
adb shell pm uninstall --user 0 com.huawei.indexsearch.observer
adb shell pm uninstall --user 0 com.huawei.lbs # HwLBService, ein Systemdienst im Bereich der Standortdienste.
adb shell pm uninstall --user 0 com.huawei.mmitest # MMITest, eine App für interne Hardwaretests und Gerätediagnose. Sie wird normalerweise nicht benötigt
adb shell pm uninstall --user 0 com.huawei.motionservice
adb shell pm uninstall --user 0 com.huawei.omacp
adb shell pm uninstall --user 0 com.huawei.rcsserviceapplication # Huawei RCS. RCS („Rich Communication Services“) ergänzt SMS
adb shell pm uninstall --user 0 com.huawei.recsys # Huawei Intelligent Recommendation System
adb shell pm uninstall --user 0 com.huawei.screenrecorder
adb shell pm uninstall --user 0 com.huawei.trustcircle
adb shell pm uninstall --user 0 com.huawei.wifieapsimplmn
adb shell pm uninstall --user 0 com.huawei.wifiprobqeservice
adb shell pm uninstall --user 0 com.huawei.health
#adb shell pm uninstall --user 0 com.huawei.hiview
#adb shell pm uninstall --user 0 com.huawei.hidisk #filemanager
#adb shell pm uninstall --user 0 com.huawei.hifolder
adb shell pm uninstall --user 0 com.huawei.hicloud
adb shell pm uninstall --user 0 com.huawei.hiviewtunnel
adb shell pm uninstall --user 0 com.huawei.himovie.overseas # HUAWEI Video für internationale Märkte
adb shell pm uninstall --user 0 com.huawei.phoneservice
adb shell pm uninstall --user 0 com.huawei.screenrecorder
adb shell pm uninstall --user 0 com.huawei.videoeditor

#adb shell pm uninstall --user 0 com.huawei.android.launcher

echo  ### "### Samsung"

adb shell pm uninstall --user 0 com.samsung.android.app.notes
adb shell pm uninstall --user 0 com.samsung.android.providers.contacts
adb shell pm uninstall --user 0 com.samsung.android.app.settings.bixby
adb shell pm uninstall --user 0 com.samsung.crane
adb shell pm uninstall --user 0 com.samsung.android.contacts
adb shell pm uninstall --user 0 com.samsung.android.app.contacts
adb shell pm uninstall --user 0 com.samsung.android.app.galaxyfinder
adb shell pm uninstall --user 0 com.samsung.android.messaging
adb shell pm uninstall --user 0 com.samsung.android.dialer
adb shell pm uninstall --user 0 com.samsung.android.contacts
adb shell pm uninstall --user 0 com.samsung.android.app.social
adb shell pm uninstall --user 0 com.samsung.android.scloud
adb shell pm uninstall --user 0 com.samsung.android.video
adb shell pm uninstall --user 0 com.samsung.android.app.memo
adb shell pm uninstall --user 0 com.samsung.android.samsungpass
adb shell pm uninstall --user 0 com.samsung.android.authfw
adb shell pm uninstall --user 0 com.samsung.android.samsungpassautofill
adb shell pm uninstall --user 0 com.samsung.android.themestore
adb shell pm uninstall --user 0 com.samsung.android.game.gametools
adb shell pm uninstall --user 0 com.samsung.android.game.gamehome
adb shell pm uninstall --user 0 com.samsung.android.gametuner.thin
adb shell pm uninstall --user 0 com.samsung.android.game.gos
adb shell pm uninstall --user 0 com.samsung.android.mateagent
adb shell pm uninstall --user 0 com.samsung.android.fmm
adb shell pm uninstall --user 0 com.samsung.android.bbc.bbcagent
adb shell pm uninstall --user 0 com.samsung.android.da.daagent
adb shell pm uninstall --user 0 com.samsung.android.drivelink.stub
adb shell pm uninstall --user 0 com.samsung.android.calendar
adb shell pm uninstall --user 0 com.samsung.android.spay
adb shell pm uninstall --user 0 com.samsung.android.spayfw
adb shell pm uninstall --user 0 com.samsung.android.hmt.vrsvc
adb shell pm uninstall --user 0 com.samsung.android.app.vrsetupwizardstub
adb shell pm uninstall --user 0 com.samsung.android.hmt.vrshell
adb shell pm uninstall --user 0 com.samsung.android.app.spage
adb shell pm uninstall --user 0 com.samsung.android.app.routines
adb shell pm uninstall --user 0 com.samsung.android.bixby.service
adb shell pm uninstall --user 0 com.samsung.android.bixby.wakeup
adb shell pm uninstall --user 0 com.samsung.android.bixby.agent
adb shell pm uninstall --user 0 com.samsung.android.bixby.agent.dummy
adb shell pm uninstall --user 0 com.samsung.android.bixbyvision.framework
adb shell pm uninstall --user 0 com.samsung.android.visionintelligence
adb shell pm uninstall --user 0 com.samsung.android.app.ledbackcover
adb shell pm uninstall --user 0 com.samsung.android.service.peoplestripe
adb shell pm uninstall --user 0 com.samsung.android.kidsinstaller
adb shell pm uninstall --user 0 com.samsung.android.app.camera.sticker.facearavatar.preload
adb shell pm uninstall --user 0 com.samsung.android.app.sbrowseredge
adb shell pm uninstall --user 0 com.samsung.android.themestore
adb shell pm uninstall --user 0 com.samsung.svoice.sync
adb shell pm uninstall --user 0 com.samsung.android.svoice
adb shell pm uninstall --user 0 com.samsung.voiceserviceplatform
adb shell pm uninstall --user 0 com.samsung.android.app.watchmanager
adb shell pm uninstall --user 0 com.samsung.android.app.watchmanagerstub
adb shell pm uninstall --user 0 com.samsung.android.voc
adb shell pm uninstall --user 0 com.samsung.android.smartmirroring
adb shell pm uninstall --user 0 com.samsung.android.smartcallprovider
adb shell pm uninstall --user 0 com.samsung.android.app.smartcapture
adb shell pm uninstall --user 0 com.samsung.android.smartface
adb shell pm uninstall --user 0 com.samsung.android.email.provider
adb shell pm uninstall --user 0 com.samsung.android.voicewakeup
adb shell pm uninstall --user 0 com.samsung.android.forest
adb shell pm uninstall --user 0 com.samsung.android.visionarapps
adb shell pm uninstall --user 0 com.samsung.android.sm.devicesecurity
adb shell pm uninstall --user 0 com.samsung.android.bbc.bbcagent
adb shell pm uninstall --user 0 com.samsung.clipboardsaveservice
adb shell pm uninstall --user 0 com.samsung.android.clipboarduiservice
adb shell pm uninstall --user 0 com.samsung.android.app.clipboardedge
adb shell pm uninstall --user 0 com.samsung.android.rubin.app
adb shell pm uninstall --user 0 com.samsung.android.service.peoplestripe
adb shell pm uninstall --user 0 com.samsung.android.aware.service
adb shell pm uninstall --user 0 com.samsung.android.app.sharelive
adb shell pm uninstall --user 0 com.samsung.android.app.reminder
adb shell pm uninstall --user 0 com.samsung.android.app.spage
adb shell pm uninstall --user 0 com.samsung.android.smartswitchassistant
adb shell pm uninstall --user 0 com.samsung.app.newtrim
adb shell pm uninstall --user 0 com.samsung.android.app.watchmanagerstub
adb shell pm uninstall --user 0 com.sec.android.daemonapp
adb shell pm uninstall --user 0 com.samsung.android.samsungpassautofill
adb shell pm uninstall --user 0 com.samsung.android.samsungpass
adb shell pm uninstall --user 0 com.samsung.android.stickercenter
adb shell pm uninstall --user 0 com.samsung.android.livestickers
adb shell pm uninstall --user 0 com.samsung.android.allshare.service.mediashare
adb shell pm uninstall --user 0 com.samsung.android.allshare.service.fileshare
adb shell pm uninstall --user 0 com.samsung.android.fast
adb shell pm uninstall --user 0 com.samsung.android.net.wifi.wifiguider
adb shell pm uninstall --user 0 com.samsung.android.app.taskedge
adb shell pm uninstall --user 0 com.samsung.android.app.appsedge
adb shell pm uninstall --user 0 com.samsung.android.homemode
adb shell pm uninstall --user 0 com.samsung.aasaservice
# adb shell pm uninstall --user 0 com.sec.android.app.bluetoothtest
adb shell pm uninstall --user 0 com.samsung.android.dqagent
adb shell pm uninstall --user 0 com.samsung.advp.imssettings
adb shell pm uninstall --user 0 com.samsung.android.mdecservice
adb shell pm uninstall --user 0 com.samsung.android.mobileservice
adb shell pm uninstall --user 0 com.samsung.android.aircommandmanager
adb shell pm uninstall --user 0 com.samsung.android.app.cocktailbarservice
adb shell pm uninstall --user 0 com.samsung.android.app.dressroom
adb shell pm uninstall --user 0 com.samsung.android.bluelightfilter
#adb shell pm uninstall --user 0 com.sec.android.app.wlantest
#adb shell pm uninstall --user 0 com.sec.mhs.smarttethering


#adb shell pm uninstall --user 0 com.samsung.desktopsystemui

#adb shell pm uninstall --user 0 com.sec.android.desktoadb shell pmode.uiservice
#adb shell pm uninstall --user 0 com.sec.android.app.desktoplauncher

echo  ### "### lineage"

adb shell pm uninstall --user 0 org.lineageos.jelly
adb shell pm uninstall --user 0 org.lineageos.audiofx
adb shell pm uninstall --user 0 org.lineageos.eleven
adb shell pm uninstall --user 0 org.lineageos.recorder
adb shell pm uninstall --user 0 org.lineageos.etar


echo
echo  "pass"

adb reboot
