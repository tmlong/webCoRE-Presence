# webCoRE Presence

https://github.com/tmlong/webCoRE

## Overview

This app is an attempt to resurrect the webCoRE presence sensor to continue providing more consistent geofencing updates. This works with the webCoRE SmartApp for creating powerful location-based automation.

<img src="https://i.imgur.com/GtXMZBt.png" width="180" height="320">

## Installation

### Device Handler

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

1. Install the [webCoRE Presence](https://play.google.com/store/apps/details?id=com.longfocus.webcorepresence) app from the Google Play Store.

## Configuration

### Add Presence Sensor

> Adding a presence sensor will appear as a new device in your SmartThings app.

1. Open the webCoRE Presence app and login to your dashboard.
1. Click the ![Add Presence Sensor](https://i.imgur.com/IhUEp9G.png "Add Presence Sensor") icon to add a presence sensor.

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