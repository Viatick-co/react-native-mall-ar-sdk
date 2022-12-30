//
//  ARView.swift
//  JarvisMallAr
//
//  Created by Hieu Nguyen on 28/12/2022.
//  Copyright © 2022 Facebook. All rights reserved.
//

import UIKit

class ARView: UIView {
//    @objc var onClickCoupon: RCTBubblingEventBlock?
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        print("setting up view");
        setupView()
    }
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        print("IOS view");
        setupView()
    }
    
    var sdkKey: String? {
      set {
        self.sdkKey = newValue
      }
        
      get {
          return self.sdkKey
      }
    }
    
    private func setupView() {
        let viewDemo = UIView()
        viewDemo.frame = CGRect(x: 50, y: 50, width: 50, height: 50)
        viewDemo.backgroundColor = (self.sdkKey == nil) ? UIColor.red : UIColor.green;

        self.isUserInteractionEnabled = true
    }
    
//    override func touchesEnded(_ touches: Set<UITouch>, with event: UIEvent?) {
//        guard let onClickCoupon = self.onClickCoupon else { return }
//
//        let params: [String : Any] = ["id":"react demo","value2":1]
//        onClickCoupon(params)
//    }
    
}
