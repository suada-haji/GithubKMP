//
//  ViewController.swift
//  GithubKMP
//
//  Created by Suada Haji on 23/01/2020.
//  Copyright © 2020 Suada Haji. All rights reserved.
//

import UIKit
import shared

class ViewController: UIViewController {

    @IBOutlet weak var greetin: UILabel!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        greetin.text = Greeting().greeting()
    }

}

