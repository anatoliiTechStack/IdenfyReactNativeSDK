package com.idenfyreactnative

import android.graphics.Color
import com.facebook.react.bridge.*
import com.idenfy.idenfySdk.CoreSdkInitialization.IdenfyController
import com.idenfy.idenfySdk.api.initialization.IdenfySettingsV2.IdenfyBuilderV2
import com.idenfy.idenfySdk.faceauthentication.api.FaceAuthenticationInitialization
import com.idenfy.idenfySdk.api.ui.IdenfyCommonColors
import com.idenfy.idenfySdk.api.ui.IdenfyToolbarUISettingsV2
import com.idenfy.idenfySdk.api.ui.IdenfyPhotoResultViewUISettingsV2
import com.idenfyreactnative.di.DIProvider
import com.idenfyreactnative.domain.IdenfyReactNativeCallbacksUseCase
import com.idenfyreactnative.domain.IdenfySdkActivityEventListener
import com.idenfyreactnative.domain.utils.GetSdkDataFromConfig

class IdenfyReactNativeModule(reactContext: ReactApplicationContext) :
  ReactContextBaseJavaModule(reactContext) {
  private val diProvider = DIProvider()
  private val idenfyReactNativeCallbacksUseCase: IdenfyReactNativeCallbacksUseCase
  private val idenfySdkActivityEventListener: IdenfySdkActivityEventListener

  init {
    idenfyReactNativeCallbacksUseCase = diProvider.idenfyReactNativeCallbacksUseCase
    idenfySdkActivityEventListener = diProvider.idenfySdkActivityEventListener
    reactContext.addActivityEventListener(idenfySdkActivityEventListener)
  }

  override fun getName(): String {
    return "IdenfyReactNative"
  }

  // Example method
  // See https://reactnative.dev/docs/native-modules-android
  @ReactMethod
  fun multiply(a: Int, b: Int, promise: Promise) {
    promise.resolve(a * b)
  }

  @ReactMethod
  fun start(config: ReadableMap, promise: Promise) {
    idenfyReactNativeCallbacksUseCase.setCallbacksReceiver(promise)

    val currentActivity = currentActivity

    if (currentActivity == null) {
      idenfyReactNativeCallbacksUseCase.getCallbackReceiver()
        ?.reject("error", Exception("Android activity does not exist"))
      idenfyReactNativeCallbacksUseCase.resetPromise()
      return
    }

    try {

      val authToken = GetSdkDataFromConfig.getSdkTokenFromConfig(config)
      val idenfySettingsV2 = GetSdkDataFromConfig.getIdenfySettingsFromConfig(config)
      idenfySettingsV2.authToken = authToken

      val idenfyColorMain = "#003188"
      val idenfyColorButton = "#003188"

      IdenfyCommonColors.idenfyMainColorV2 = Color.parseColor(idenfyColorMain)
      IdenfyCommonColors.idenfyMainDarkerColorV2 = Color.parseColor(idenfyColorMain)
      IdenfyCommonColors.idenfyGradientColor1V2 = Color.parseColor(idenfyColorButton)
      IdenfyCommonColors.idenfyGradientColor2V2 = Color.parseColor(idenfyColorButton)

      IdenfyToolbarUISettingsV2.idenfyDefaultToolbarBackgroundColor = Color.parseColor(idenfyColorMain)

      IdenfyToolbarUISettingsV2.idenfyDefaultToolbarBackIconTintColor = IdenfyCommonColors.idenfyBlack
      IdenfyToolbarUISettingsV2.idenfyDefaultToolbarLogoIconTintColor = IdenfyCommonColors.idenfyBlack

      IdenfyToolbarUISettingsV2.idenfyLanguageSelectionToolbarLanguageSelectionIconTintColor = IdenfyCommonColors.idenfyBlack
      IdenfyToolbarUISettingsV2.idenfyLanguageSelectionToolbarCloseIconTintColor = IdenfyCommonColors.idenfyBlack

      IdenfyCommonColors.idenfyPhotoResultDetailsCardBackgroundColorV2 = Color.parseColor("#003188")
      IdenfyPhotoResultViewUISettingsV2.idenfyPhotoResultViewDetailsCardTitleColor = Color.parseColor(idenfyColorButton)

      IdenfyController.getInstance().initializeIdenfySDKV2WithManual(
        currentActivity,
        IdenfyController.IDENFY_REQUEST_CODE,
        idenfySettingsV2
      )
    }

    //Unexpected exceptions
    catch (e: Throwable) {
      e.printStackTrace()
      idenfyReactNativeCallbacksUseCase.getCallbackReceiver()?.reject(
        "error",
        Exception("Unexpected error. Verify that config is structured correctly.")
      )
      idenfyReactNativeCallbacksUseCase.resetPromise()
      return
    }

  }

  @ReactMethod
  fun startFaceReAuth(config: ReadableMap, promise: Promise) {
    idenfyReactNativeCallbacksUseCase.setCallbacksReceiver(promise)

    val currentActivity = currentActivity

    if (currentActivity == null) {
      idenfyReactNativeCallbacksUseCase.getCallbackReceiver()
        ?.reject("error", Exception("Android activity does not exist"))
      idenfyReactNativeCallbacksUseCase.resetPromise()
      return
    }

    try {

      val authToken = GetSdkDataFromConfig.getSdkTokenFromConfig(config)
      val immediateRedirect = GetSdkDataFromConfig.getImmediateRedirectFromConfig(config)
      val faceAuthUISettings = GetSdkDataFromConfig.getFaceAuthSettingsFromConfig(config)
      val faceReauthenticationInitialization =
        FaceAuthenticationInitialization(authToken, immediateRedirect, faceAuthUISettings)
      IdenfyController.getInstance().initializeFaceAuthenticationSDKV2(
        currentActivity,
        IdenfyController.IDENFY_REQUEST_CODE,
        faceReauthenticationInitialization
      )
    }

    //Unexpected exceptions
    catch (e: Throwable) {
      e.printStackTrace()
      idenfyReactNativeCallbacksUseCase.getCallbackReceiver()?.reject(
        "error",
        Exception("Unexpected error. Verify that config is structured correctly.")
      )
      idenfyReactNativeCallbacksUseCase.resetPromise()
      return
    }

  }

}
