import * as React from 'react';

import { StyleSheet, View } from 'react-native';
import { ARView } from 'react-native-jarvis-mall-ar';

export default function App() {
  const SDK_KEY = 'JarvisKey123123';
  return (
    <View style={styles.container}>
      <ARView
        sdkKey={SDK_KEY}
        onCouponClick={() => {
          console.log('RN side log');
        }}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  box: {
    width: 60,
    height: 60,
    marginVertical: 20,
  },
});
