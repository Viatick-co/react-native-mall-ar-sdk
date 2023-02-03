import * as React from 'react';

import { StyleSheet, View } from 'react-native';
// import { PixelRatio } from 'react-native';
import { ARView } from 'react-native-jarvis-mall-ar';

export default function App() {
  const SDK_KEY = 'jvm3c853ef1974550065c65eb2b96738';

  React.useEffect(() => {
    // const viewId = findNodeHandle(ref.current);
    // createFragment(viewId as number);
  }, []);

  return (
    <View style={styles.container}>
      <ARView
        style={styles.arView}
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
  arView: {
    flex: 1,
  },
});
