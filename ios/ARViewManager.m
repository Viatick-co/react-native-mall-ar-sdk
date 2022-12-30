#import <React/RCTViewManager.h>
#import <React/RCTBridgeModule.h>

@interface RCT_EXTERN_MODULE(ARViewManager, RCTViewManager)

RCT_EXPORT_VIEW_PROPERTY(sdkKey, NSString)
RCT_EXPORT_VIEW_PROPERTY(onStart, RCTBubblingEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onStop, RCTBubblingEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onClickCoupon, RCTBubblingEventBlock)

@end
