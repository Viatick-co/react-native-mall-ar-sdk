import UIKit

@objc (ARViewManager)
class ARViewManager: RCTViewManager {
 
  override static func requiresMainQueueSetup() -> Bool {
    return true
  }
 
  override func view() -> UIView! {
      print("1 ar mng");
    return ARView()
  }
 
}
