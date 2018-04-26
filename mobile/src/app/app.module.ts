import { NgModule, ErrorHandler } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { IonicApp, IonicModule, IonicErrorHandler } from 'ionic-angular';
import { MyApp } from './app.component';

import { SettingsPage } from '../pages/settings/settings';
import { HomePage } from '../pages/home/home';
import { TabsPage } from '../pages/tabs/tabs';

import { StatusBar } from '@ionic-native/status-bar';
import { SplashScreen } from '@ionic-native/splash-screen';
import { InAppBrowser } from '@ionic-native/in-app-browser';
import { BrowserTab } from '@ionic-native/browser-tab';

import { NetworkInterface } from '@ionic-native/network-interface';
import { Geolocation } from '@ionic-native/geolocation';

import { AACAuth } from '../services/aac';
import { HttpModule, Http } from '@angular/http';
import { LoginPage } from '../pages/login/login';

import { TranslateModule, TranslateLoader } from '@ngx-translate/core';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { HistoryPage } from '../pages/history/history';
import { LocationChecker } from '../services/location';
import { AttendanceService } from '../services/attendance';


export function HttpLoaderFactory(http: HttpClient) {
  return new TranslateHttpLoader(http, './assets/i18n/', '.json');
}

@NgModule({
  declarations: [
    MyApp,
    HistoryPage,
    SettingsPage,
    HomePage,
    TabsPage,
    LoginPage
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    HttpModule,
    TranslateModule.forRoot({loader: { 
      provide: TranslateLoader,
      useFactory: HttpLoaderFactory,
      deps: [HttpClient]}}),
      IonicModule.forRoot(MyApp),      
  ],
  bootstrap: [IonicApp],
  entryComponents: [
    MyApp,
    HistoryPage,
    SettingsPage,
    HomePage,
    TabsPage,
    LoginPage
  ],
  providers: [
    StatusBar,
    SplashScreen,
    InAppBrowser,
    BrowserTab,
    {provide: ErrorHandler, useClass: IonicErrorHandler},
    AACAuth,
    NetworkInterface,
    Geolocation,
    LocationChecker,
    AttendanceService
  ]
})
export class AppModule {}
