//
//  ARView.swift
//  JarvisMallAr
//
//  Created by Hieu Nguyen on 28/12/2022.
//  Copyright © 2022 Facebook. All rights reserved.
//

import UIKit
import IndoorAtlas

class ARView: UIView {
    @objc var onClickCoupon: RCTBubblingEventBlock?
    private var _sdkKey:String?
    var sdkKeyText = UILabel(frame: CGRect(x: 0, y: 0, width: 200, height: 21))
    lazy var customView = UIView(frame: CGRect(x: 50, y: 50, width: 220, height: 220))
    
    @objc func someAction(_ sender:UITapGestureRecognizer){
        print("view was clicked")
        guard let onClickCoupon = self.onClickCoupon else { return }

        let params: [String : Any] = ["id":"react demo","value2":1]
        onClickCoupon(params)
    }
    
    @objc var sdkKey: NSString? {
      set {
          _sdkKey = newValue as? String
          sdkKeyText.text = newValue as? String;
          sdkKeyText.textColor = UIColor.green
      }
      get {
          return _sdkKey as NSString?
      }
    }
    
    override init(frame: CGRect) {
        var arViewController = ARViewController.init()

        super.init(frame: frame)
        self.addSubview(arViewController.view)
//        setupView()
       
    }
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }
    
    private func setupView() {
        print("inside settup", self._sdkKey);

        let gesture = UITapGestureRecognizer(target: self, action:  #selector (self.someAction (_:)))
        
        customView.isUserInteractionEnabled = true
//        customView.addGestureRecognizer(gesture)
        customView.addSubview(sdkKeyText);
        customView.backgroundColor = UIColor.red
        
        self.addSubview(customView)
    }
        
//    override func touchesEnded(_ touches: Set<UITouch>, with event: UIEvent?) {
//        guard let onClickCoupon = self.onClickCoupon else { return }
//
//        let params: [String : Any] = ["id":"react demo","value2":1]
//        onClickCoupon(params)
//    }
    
}
