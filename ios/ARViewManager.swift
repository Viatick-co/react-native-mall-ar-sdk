import UIKit
import UserNotifications

@objc (ARViewManager)
class ARViewManager: RCTViewManager {

    override static func requiresMainQueueSetup() -> Bool {
        return true
    }

    override func view() -> UIView! {
        return ARView()
    }

}
