package net.kenneho.runnow;

import org.acra.ACRA;
import static org.acra.ReportField.*; 

import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import com.activeandroid.ActiveAndroid;

@ReportsCrashes(formKey = "", // For Google spreadsheet, will not be used
mailTo = "kenneho@gmail.com",
customReportContent = {APP_VERSION_CODE, ANDROID_VERSION, PHONE_MODEL, CUSTOM_DATA,
        STACK_TRACE, LOGCAT, AVAILABLE_MEM_SIZE, REPORT_ID, APPLICATION_LOG, PACKAGE_NAME, FILE_PATH,
        BUILD, BRAND, PRODUCT, TOTAL_MEM_SIZE, INITIAL_CONFIGURATION, CRASH_CONFIGURATION, DISPLAY,
        USER_COMMENT, USER_APP_START_DATE, USER_CRASH_DATE, DUMPSYS_MEMINFO, EVENTSLOG, RADIOLOG, IS_SILENT,
        DEVICE_ID, INSTALLATION_ID, DEVICE_FEATURES, ENVIRONMENT, SETTINGS_SYSTEM, SETTINGS_SECURE,
        SHARED_PREFERENCES, MEDIA_CODEC_LIST, THREAD_DETAILS},
mode = ReportingInteractionMode.TOAST,
resToastText = R.string.crash_toast_text)
public class NextDeparture extends com.activeandroid.app.Application {

	   @Override
       public void onCreate() {
           super.onCreate();

           // The following line triggers the initialization of ACRA
           ACRA .init(this);
           ActiveAndroid.initialize(this);
       }
	
}
