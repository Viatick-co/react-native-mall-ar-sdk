import { NativeModules, Platform, ViewStyle } from 'react-native';
import { requireNativeComponent } from 'react-native';
import React, { LegacyRef } from "react";

const LINKING_ERROR =
  `The package 'react-native-jarvis-mall-ar' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

const JarvisMallAr = NativeModules.JarvisMallAr
  ? NativeModules.JarvisMallAr
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );
const NATIVE_VIEW_NAME = 'ARView';
export const ARViewRaw = requireNativeComponent(NATIVE_VIEW_NAME);
type ARViewProps = {
  sdkKey: string;
  style?: ViewStyle;
  onStart?: () => void;
  onEnd?: () => void;
  onCouponClick?: () => void;
  ref?: LegacyRef<any>;
};

const ARView: React.FC<ARViewProps> = (props) => {
  console.log('props', props);
  return <ARViewRaw {...props} />;
};

export function multiply(a: number, b: number): Promise<number> {
  return JarvisMallAr.multiply(a, b);
}

export { ARView };
