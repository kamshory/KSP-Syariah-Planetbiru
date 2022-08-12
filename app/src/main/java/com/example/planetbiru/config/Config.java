package com.example.planetbiru.config;

public class Config {

    private static String applicationVersion              = "1.0.0";
    private static boolean useNotification                = false;
    private static String showPinURL                      = "https://www.planetbiru.com/otp.php?otp=%s";
    private static long waitForNextJob                    = 5000;
    private static String appProvider                     = "com.example.planetbiru.provider";
    private static String applicationName                 = "Planetbiru WebView; com.example.planetbiru; ver 1.0.0";
    private static boolean toastDeviceRegistration        = false;
    private static boolean alwaysRegisterDevice           = true;

    private static String pushServerAddress               = "server.planetbiru.com";
    private static int pushServerPort                     = 96;
    private static boolean pushSSL                        = false;
    private static String apiKey                          = "PLANETBIRU";
    private static String apiClientPassword               = "123456";

    private static String urlUserInfo                     = "https://www.planetbiru.com/lib.push/1.0.0/user-information/";
    private static String urlDeviceRegistration           = "https://www.planetbiru.com/lib.push/1.0.0/device-resgitration/";
    private static String reloadURL                       = "file:///android_asset/www/reload.html";
    private static String errorPageURL                    = "file:///android_asset/www/error.html";

    private static String errorPageMessage                = "Halaman tidak dapat dimuat. Pastikan koneksi internet berjalan sebagaimana mestinya.";
    private static String errorPageMessageFormat          = "Halaman %s tidak dapat dimuat. Pastikan koneksi internet berjalan sebagaimana mestinya.";
    private static String homeURL                         = "file:///android_asset/www/index.html";

    private static String internalHosts                   = "";
    private static String androidID                       = "";
    private static String cookie                          = "";
    private static String acceptedFileType                = "image/*";
    private static boolean allowMultipleFile              = false;
    private static boolean allowTakeImage                 = true;
    private static boolean askPermissionGetLocation       = false;
    private static boolean rememberPermissionGetLocation  = false;
    private static String encryptionKey                   = "BDE540BD7E96ECAB33D0216EF003F53C";

    public static String getApplicationName() {
        return applicationName;
    }

    public static String getAndroidID() {
        return androidID;
    }

    public static void setAndroidID(String androidID) {
        Config.androidID = androidID;
    }

    public static String getCookie() {
        return cookie;
    }

    public static void setCookie(String cookie) {
        Config.cookie = cookie;
    }

    public static String getInternalHosts() {
        return internalHosts;
    }

    public static void setInternalHosts(String internalHosts) {
        Config.internalHosts = internalHosts;
    }

    public static String getHomeURL() {
        return homeURL;
    }

    public static void setHomeURL(String homeURL) {
        Config.homeURL = homeURL;
    }

    public static String getReloadURL() {
        return reloadURL;
    }

    public static void setReloadURL(String reloadURL) {
        Config.reloadURL = reloadURL;
    }

    public static String getErrorPageURL() {
        return errorPageURL;
    }

    public static void setErrorPageURL(String errorPageURL) {
        Config.errorPageURL = errorPageURL;
    }

    public static String getErrorPageMessage() {
        return errorPageMessage;
    }

    public static void setErrorPageMessage(String errorPageMessage) {
        Config.errorPageMessage = errorPageMessage;
    }

    public static String getPushServerAddress() {
        return pushServerAddress;
    }

    public static void setPushServerAddress(String pushServerAddress) {
        Config.pushServerAddress = pushServerAddress;
    }

    public static int getPushServerPort() {
        return pushServerPort;
    }

    public static void setPushServerPort(int pushServerPort) {
        Config.pushServerPort = pushServerPort;
    }

    public static boolean isPushSSL() {
        return pushSSL;
    }

    public static void setPushSSL(boolean pushSSL) {
        Config.pushSSL = pushSSL;
    }

    public static String getApiKey() {
        return apiKey;
    }

    public static void setApiKey(String apiKey) {
        Config.apiKey = apiKey;
    }

    public static String getApiClientPassword() {
        return apiClientPassword;
    }

    public static void setApiClientPassword(String apiClientPassword) {
        Config.apiClientPassword = apiClientPassword;
    }

    public static String getUrlUserInfo() {
        return urlUserInfo;
    }

    public static void setUrlUserInfo(String urlUserInfo) {
        Config.urlUserInfo = urlUserInfo;
    }

    public static String getUrlDeviceRegistration() {
        return urlDeviceRegistration;
    }

    public static void setUrlDeviceRegistration(String urlDeviceRegistration) {
        Config.urlDeviceRegistration = urlDeviceRegistration;
    }

    public static String getErrorPageMessageFormat() {
        return errorPageMessageFormat;
    }

    public static void setErrorPageMessageFormat(String errorPageMessageFormat) {
        Config.errorPageMessageFormat = errorPageMessageFormat;
    }

    public static boolean isAlwaysRegisterDevice() {
        return alwaysRegisterDevice;
    }

    public static void setAlwaysRegisterDevice(boolean alwaysRegisterDevice) {
        Config.alwaysRegisterDevice = alwaysRegisterDevice;
    }

    public static String getAcceptedFileType() {
        return acceptedFileType;
    }

    public static void setAcceptedFileType(String acceptedFileType) {
        Config.acceptedFileType = acceptedFileType;
    }

    public static boolean isAllowMultipleFile() {
        return allowMultipleFile;
    }

    public static void setAllowMultipleFile(boolean allowMultipleFile) {
        Config.allowMultipleFile = allowMultipleFile;
    }

    public static boolean isAllowTakeImage() {
        return allowTakeImage;
    }

    public static void setAllowTakeImage(boolean allowTakeImage) {
        Config.allowTakeImage = allowTakeImage;
    }

    public static boolean isAskPermissionGetLocation() {
        return askPermissionGetLocation;
    }

    public static void setAskPermissionGetLocation(boolean askPermissionGetLocation) {
        Config.askPermissionGetLocation = askPermissionGetLocation;
    }

    public static boolean isRememberPermissionGetLocation() {
        return rememberPermissionGetLocation;
    }

    public static void setRememberPermissionGetLocation(boolean rememberPermissionGetLocation) {
        Config.rememberPermissionGetLocation = rememberPermissionGetLocation;
    }

    public static boolean isToastDeviceRegistration() {
        return toastDeviceRegistration;
    }

    public static void setToastDeviceRegistration(boolean toastDeviceRegistration) {
        Config.toastDeviceRegistration = toastDeviceRegistration;
    }

    public static String getAppProvider() {
        return appProvider;
    }

    public static long getWaitForNextJob() {
        return waitForNextJob;
    }

    public static String getShowPinURL() {
        return showPinURL;
    }

    public static void setShowPinURL(String showPinURL) {
        Config.showPinURL = showPinURL;
    }

    public static boolean isUseNotification() {
        return useNotification;
    }

    public static void setUseNotification(boolean useNotification) {
        Config.useNotification = useNotification;
    }

    public static String getApplicationVersion() {
        return applicationVersion;
    }

    public static void setApplicationVersion(String applicationVersion) {
        Config.applicationVersion = applicationVersion;
    }

    public static String getEncryptionKey() {
        return encryptionKey;
    }

    public static void setEncryptionKey(String encryptionKey) {
        Config.encryptionKey = encryptionKey;
    }
}
