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
    var arViewController: ARViewController
    private var _sdkKey:String?
    var sdkKeyText = UILabel(frame: CGRect(x: 0, y: 0, width: 200, height: 21))
    private var alert: UIAlertController!

    @objc func someAction(_ sender:UITapGestureRecognizer){
        print("view was clicked")
        guard let onClickCoupon = self.onClickCoupon else { return }

        let params: [String : Any] = ["id":"react demo","value2":1]
        onClickCoupon(params)
    }
    
    @objc var sdkKey: NSString? {
      set {
          _sdkKey = newValue as? String
          if(newValue != nil && newValue != "") {
              self.loadApplication(apiKey: newValue as! String)
          }
          sdkKeyText.text = newValue as? String;
          sdkKeyText.textColor = UIColor.green
      }
      get {
          return _sdkKey as NSString?
      }
    }
    
    override init(frame: CGRect) {
        arViewController = ARViewController.init()
        super.init(frame: frame)
        self.isUserInteractionEnabled = true;
        self.authenticateIALocationManager();
    }
    
    required init?(coder aDecoder: NSCoder) {
        arViewController = ARViewController.init()
         super.init(coder: aDecoder)
     }
    
    override func layoutSubviews() {
        super.layoutSubviews()
        self.arViewController.view.frame = self.bounds
     }
    
    struct AccountDetail: Codable {
      var id : Int64
      var name: String
      var description: String?
    }
    
    private func handleError() {
        DispatchQueue.main.async {
            let errorView = UIView(frame: CGRect(x: 0, y: 200, width: self.frame.size.width, height: 220))
            let title = UILabel(frame: CGRect(x: 10, y: 0, width: self.frame.size.width, height: 100))
            let errorText = UITextField(frame: CGRect(x: 10, y: 100, width: self.frame.size.width, height: 30))
            
            title.text = "Error!"
            title.font = title.font?.withSize(50)
            title.textColor = .red
            
            errorText.text = "Your sdk key is invalid, please contact viatick.com."
            errorText.font = errorText.font?.withSize(16)
            errorText.textColor = .darkGray
            errorText.adjustsFontSizeToFitWidth = false
            
            errorView.addSubview(title)
            errorView.addSubview(errorText)
            
            self.addSubview(errorView)
            self.bringSubviewToFront(errorView)
        }
    }
    
    private func loadApplication(apiKey : String) {
        if(apiKey == "") {
            self.handleError()
            return;
        }
        let apiHost = "https://jarvis.viatick.com/apis";
        let url = URL(string: apiHost + "/account/application/detail");

        var request = URLRequest(url: url!);
        request.httpMethod = "GET";
        request.setValue(apiKey, forHTTPHeaderField: "Access-Token");
          
        let task = URLSession.shared.dataTask(with: request, completionHandler: { (data, response, error) in
          if let error = error {
            print("Error with fetching films: \(error)")
              self.handleError()
            return
          }
          
          guard let httpResponse = response as? HTTPURLResponse,
                (200...299).contains(httpResponse.statusCode) else {
            print("Error with the response, unexpected status code: \(response)")
              self.handleError()
            return
          }

          if let data = data,
             let accountDetail = try? JSONDecoder().decode(AccountDetail.self, from: data){
              DispatchQueue.main.async {
                  self.addSubview(self.arViewController.view)
              }
              print("Account detail --- ", accountDetail);
            return;
          }
        })

        task.resume();
    }
    
    func authenticateIALocationManager() {
        // Get IALocationManager shared instance
        let manager = IALocationManager.sharedInstance()

        // Set IA API key
        manager.setApiKey("ddc70c2b-e171-40fc-8ee9-85cafeeedad3", andSecret: "Pno9/mCAiPJwF8JNJXXmv/gp/e1xnLJ5gM67ggYH4g7x2SEEZsHmKdiX3d1+cOmfpofW2kLmyy7zatiePq6XG9hKoq45ZD2ThNezGZb7Yq2MdL2beLcqqcl/8POcQA==")
        
        // Allows testing IndoorAtlas in background mode
        // See: https://developer.apple.com/documentation/xcode/configuring-background-execution-modes
        if let modes = Bundle.main.object(forInfoDictionaryKey: "UIBackgroundModes") as! Array<String>? {
            if (modes.contains("location")) {
                manager.allowsBackgroundLocationUpdates = true
            }
        }
    }
    
}
