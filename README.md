# react-native-jarvis-mall-ar

React native mall ar sdk for indoor navigation

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

#### Register permissions and Service in `AndroidManifest.xml`.

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

### iOS
##### Install pods:

`cd ios` \
`pod install`

## Usage

```js
import { ARView } from 'react-native-jarvis-mall-ar';

// ...
<ARView
    sdkKey={""}
    onStart={()=>{}}
    onStop={() => {}}
    onCouponClick={() => {}}
/>
```

#### Arguments

- `sdkKey` : `string` | Your account SDK Key, please contact `viatick.com` to create an account.
- `onStart` : Callback method when start AR feature.
- `onStop` : Callback method when stop AR feature.
- `onCouponClick` | `(couponId : string) => {}` : Callback method when user interact with the coupon on AR side.

## License

MIT

---

