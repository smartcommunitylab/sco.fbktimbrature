import { Component } from '@angular/core';
import { Platform } from 'ionic-angular';
import { StatusBar } from '@ionic-native/status-bar';
import { SplashScreen } from '@ionic-native/splash-screen';

import { TabsPage } from '../pages/tabs/tabs';
import { HomePage } from '../pages/home/home';
import { AACAuth, AACAuthConfig } from '../services/aac';
import { LoginPage } from '../pages/login/login';
import { TranslateService } from '@ngx-translate/core';

@Component({
  templateUrl: 'app.html'
})
export class MyApp {
  rootPage: any = null;

  readonly config : AACAuthConfig = {
    client_id: '7b4f9b2a-71f6-412d-93e6-030c14910083',
    client_secret: 'e8a302da-f9dc-4515-ac91-abf7940d58ad',
    redirect_uri: 'https://tn.smartcommunitylab.it/timbrature/oauth',
    scopes: 'profile.basicprofile.me profile.accountprofile.me'
  }


  constructor(private platform: Platform, private statusBar: StatusBar, private splashScreen: SplashScreen, private translate: TranslateService, private aac: AACAuth) {
    this.initTranslate();
    const endpoint = 'https://am-test.smartcommunitylab.it/aac';
    // const endpoint = 'http://192.168.31.217:8080/aac';
    aac.config(this.config, endpoint);

    this.platform.ready().then(() => {

      // Okay, so the platform is ready and our plugins are available.
      // Here you can do any higher level native things you might need.
      statusBar.styleDefault();
      splashScreen.hide();
      this.rootPage = aac.isLoggedIn() ? TabsPage : LoginPage;
    });
  }

  initTranslate() {
    // Set the default language for translation strings, and the current language.
    this.translate.setDefaultLang('en');

    if (this.translate.getBrowserLang() !== undefined) {
      this.translate.use(this.translate.getBrowserLang());
    } else {
      this.translate.use('en'); // Set your language here
    }
  }
}