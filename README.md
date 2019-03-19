# webCoRE Presence

https://github.com/tmlong/webCoRE

## Overview

This app is an attempt to breathe new life into the webCoRE presence sensor to continue providing more consistent geofencing updates. This works with the webCoRE SmartApp for creating powerful location-based automation.

<img src="https://i.imgur.com/whfxHI4.png" width="180" height="320"/>&nbsp;<img src="https://i.imgur.com/3iV7W84.png" width="180" height="320"/>&nbsp;<img src="https://i.imgur.com/GtXMZBt.png" width="180" height="320"/>

## Installation

### Device Handler

> Requires the webCoRE SmartApp to be installed.

1. Within the SmartThings IDE, click on *'My Device Handlers'*.
1. Follow the steps below depending on how you install webCoRE.

#### Github Integration
1. Click the *'Update from Repo'* button.
1. Click the *'webCoRE (master)'* option from the drop-down list.
1. Select the *'webCoRE Presence Sensor*' device type from the list.
1. Select *'Publish'*, then click *'Execute Update'*.
1. Ensure the *'webCoRE Presence Sensor'* is listed and marked with a *'Published'* status.

#### Source Code
1. Click the *'+ Create New Device Handler'* button.
1. Select the *'From Code'* tab and paste in the source code from [here](https://raw.githubusercontent.com/ady624/webCoRE/master/devicetypes/ady624/webcore-presence-sensor.src/webcore-presence-sensor.groovy).
1. Click *'Create'*.
1. Click *'Publish'*, then click *'For Me'*.
1. Ensure the *'webCoRE Presence Sensor'* is listed and marked with a *'Published'* status.

### Android App

> Requires access to the device's location.

1. Install the [webCoRE Presence](https://play.google.com/store/apps/details?id=com.longfocus.webcorepresence) app from the Google Play Store.

## Configuration

### Presence Sensor

> Adding a presence sensor will appear as a new device in your SmartThings app.

1. Open the webCoRE Presence app and login to your dashboard.
1. Click the [![Add Presence Sensor](https://i.imgur.com/IhUEp9G.png "Add Presence Sensor")](#presence-sensor) icon to add a presence sensor.
   * As an alternative, the presence sensor can be setup under the *'Settings'* > *'Places'* > *'Setup sensor'* option from within the dashboard. Either option will work just fine and have the same end result.
   * Note: Once the presence sensor is configured, the *'Setup sensor'* option from within the dashboard will no longer be available until the app is reinstalled.
1. Location updates will be started immediately.

## Options

> Location updates are polled with a minimum interval of 30s and distance of 100m.

### App

<img src="https://i.imgur.com/snrMbMx.png" width="360" height="49"/>
<img src="https://i.imgur.com/y4UC1xa.png" width="360" height="49"/>

* [![Add Presence Sensor](https://i.imgur.com/IhUEp9G.png "Add Presence Sensor")](#app) Add a presence sensor.
  * This option will overwrite the existing presence sensor, if one already exists.
* [![Location Updates Started](https://i.imgur.com/bSJt5vN.png "Location Updates Started")](#app) Location updates started.
  * Clicking this icon will stop location updates.
  * This option will appear once a presence sensor is configured.
* [![Location Updates Stopped](https://i.imgur.com/1lkoGf2.png "Location Updates Stopped")](#app) Location updates stopped.
  * Clicking this icon will start location updates.
  * This option will appear once a presence sensor is configured.
* [![Refresh Location](https://i.imgur.com/7vhVaw6.png "Refresh Location")](#app) Refresh location.
  * Clicking this icon will immediately update the location.
  * This option will appear once a presence sensor is configured.

### Notification

<img src="https://i.imgur.com/97Jpbij.png" width="360" height="20"/>
<img src="https://i.imgur.com/MJs7lab.png" width="360" height="20"/>
<img src="https://i.imgur.com/XEKfnmi.png" width="360" height="20"/>

* [![Location Updates Started (Away)](https://i.imgur.com/CJVO5eU.png "Location Updates Started (Away)")](#notification) Location updates started (Away).
* [![Location Updates Started (Home)](https://i.imgur.com/xUwOCC9.png "Location Updates Started (Home)")](#notification) Location updates started (Home).
* [![Location Updates Stopped](https://i.imgur.com/fN6nkRq.png "Location Updates Stopped")](#notification) Location updates stopped.

## Links

- https://github.com/ady624/webCoRE
- https://community.webcore.co/t/install-instructions-webcore-presence-sensor-on-your-android-beta/1324

License
=======

    Copyright 2019 Todd Long

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.