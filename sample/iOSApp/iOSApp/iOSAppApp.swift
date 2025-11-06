//
//  iOSAppApp.swift
//  iOSApp
//
//  Created by Amar Patil on 05/11/25.
//

import SwiftUI
import PostFramework

@main
struct iOSAppApp: App {
    
    init() {
        KoinKt.doInitDefaultKoin()
    }
    
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
