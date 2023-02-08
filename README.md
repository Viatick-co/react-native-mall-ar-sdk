# react-native-jarvis-mall-ar

## Description
Digital features enhance the shopping experience for visitors and provide new efficiencies for mall owners and operators. With react-native-jarvis-mall-sdk, shopping malls can build completely custom indoor mapping experiences for both Android and IOS.

## Installation

```sh
npm install react-native-jarvis-mall-ar
```
```sh
yarn add react-native-jarvis-mall-ar
```
Auto linking when using React Native >= 0.60


### Android

Steps to setup in Android

#### 1. Register permissions and Service in `AndroidManifest.xml`.

Example:

```js
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
          package="com.jarvisappsdktest">

  <!-- Require android phone supports Bluetooth Low Energy -->
  <uses-feature
    android:name="android.hardware.bluetooth_le"
    android:required="true" />

  <!-- Required permissions -->
  <uses-permission android:name="android.permission.INTERNET" />
  <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
  <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
  <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
  <uses-permission android:name="android.permission.ACCESS_BACKGROUND_LOCATION" />
  <uses-permission android:name="android.permission.BLUETOOTH" />
  <uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
  <uses-permission android:name="android.permission.BLUETOOTH_SCAN" />
  <uses-permission android:name="android.permission.CAMERA" />

  <application
    android:name=".MainApplication">
    <!-- Your code -->

    <!-- Register SDK service here -->
    <service
      android:name="com.reactnativejarvistemplateappsdk.services.BleScannerService"
      android:exported="false"
      android:foregroundServiceType="location|dataSync" />
  </application>
</manifest>
```

##### 2. Update build gradle in `android/build.gradle` inside `allprojects -> repositories -> maven` add the package:

          `url "https://dl.cloudsmith.io/public/indooratlas/mvn-public/maven/"`

### iOS

##### 1. Update Info.Plist file

`<key>NSBluetoothPeripheralUsageDescription</key>`\
`<string>The app uses bluetooth to transfer data to a neighbouring device.</string>`\
`<key>NSBluetoothAlwaysUsageDescription</key>`\
`<string>The app may frequently use bluetooth to transfer data to peers.</string>`\
`<key>NSLocationAlwaysAndWhenInUseUsageDescription</key>`\
`<string>The app may frequently use location to detect position of users</string>`\
`<key>NSLocationAlwaysUsageDescription</key>`\
`<string>The app may frequently use location to detect position of users</string>`\
`<key>NSMotionUsageDescription</key>`\
`<string>The app need to access motion of users device</string>`\
`<key>NSCameraUsageDescription</key>`\
`<string>The app need to access camera permission</string>`


##### 2. Install pods:

`cd ios` \
`pod install`

## Usage

```js
import { ARView } from 'react-native-jarvis-mall-ar';

// ...
<ARView
    sdkKey={"sdkKey"}
    onCouponClick={() => {}}
/>
```

#### Arguments

- `sdkKey` : `string` | Your account SDK Key, please contact `viatick.com` to create an account.
- `onStart` : Callback method when start AR feature.
- `onStop` : Callback method when stop AR feature.
- `onCouponClick` | `(couponId : string) => {}` : Callback method when user interact with the coupon on AR side.

## Example

`https://github.com/Viatick-co/react-native-mall-ar-sdk/tree/main/example`
## License
Viatick team \
Website : `https://viatick.com/` \
Email: `enquiry@viatick.com`
---

