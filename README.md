[![Build Status](https://app.bitrise.io/app/ae8aa5c99e71ca88/status.svg?token=JZQLgdt_X7c05LyelKtL_w&branch=master)](https://app.bitrise.io/app/ae8aa5c99e71ca88) [![Maintainability](https://api.codeclimate.com/v1/badges/890423da31c02a714275/maintainability)](https://codeclimate.com/github/btelman96/ControlSDK/maintainability)[ ![Download](https://api.bintray.com/packages/btelman96/maven/ControlSDK/images/download.svg) ](https://bintray.com/btelman96/maven/ControlSDK/_latestVersion)
# ControlSDK
Control API for Android devices to control IoT devices, electronics boards (Arduino, Serial Motor Controllers, etc), or headless Android devices

# Work In Progress

Code in here at the moment is not ready for release. Although functional, future changes may break current functionality or the API may change a lot

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


Note: This only applies when using the streaming module
This software uses code of <a href=http://ffmpeg.org>FFmpeg</a> licensed under the <a href=http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html>LGPLv2.1</a> and its source can be downloaded <a href=https://github.com/btelman96/ffmpeg-android>here</a>
