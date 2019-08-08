[![Build Status](https://app.bitrise.io/app/ae8aa5c99e71ca88/status.svg?token=JZQLgdt_X7c05LyelKtL_w&branch=master)](https://app.bitrise.io/app/ae8aa5c99e71ca88) [![Maintainability](https://api.codeclimate.com/v1/badges/890423da31c02a714275/maintainability)](https://codeclimate.com/github/btelman96/ControlSDK/maintainability)
# ControlSDK
Control API for Android devices to control IoT devices, electronics boards (Arduino, Serial Motor Controllers, etc), or headless Android devices

# Work In Progress

Code in here at the moment is not ready for release. Although functional, future changes may break current functionality or the API may change a lot

This is the end goal of this project.

[ControlSDK Flow (PDF)](docs/ControlSDK.pdf)

The one thing that has changed is that the external apps being able to communicate with it may be more limited than I wanted, and may not be addressed in the first few versions of the sdk

# Use cases

## Telepresence through any service easily.

- Custom app handles credentials to service

- Main app controls the motors, sensors, TTS, and can stream video from itself if supported.

## remote control of wireless devices

- Custom app contains virtual joystick or a way to plug in an actual controller, with bluetooth or through USB

- ControlSDK app can handle controlling motors

- Custom app can communicate with another device with the ControlSDK. In this scenario, 
the ControlSDK on the main device will relay data to the second device.

## Turning off notifications for when internal exceptions occur

Add this to your application class

`ControlSDKService.allowNotificationForExceptions = false`
