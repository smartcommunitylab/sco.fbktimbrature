import { Component } from '@angular/core';
import { IonicPage, NavController, NavParams, Platform, ToastController } from 'ionic-angular';
import { AACAuth } from '../../services/aac';
import { TabsPage } from '../tabs/tabs';
import { TranslateService } from '@ngx-translate/core';

/**
 * Generated class for the LoginPage page.
 *
 * See https://ionicframework.com/docs/components/#navigation for more info on
 * Ionic pages and navigation.
 */

@Component({
  selector: 'page-login',
  templateUrl: 'login.html',
})
export class LoginPage {

  constructor(public navCtrl: NavController, public auth: AACAuth, private platform: Platform, public toastCtrl: ToastController, public translate: TranslateService) {
  }

  ionViewDidLoad() {
    console.log('ionViewDidLoad LoginPage');
  }

  login() {
    this.auth.login('google').then(tokenData => {
      this.auth.getAccountProfile().then(profile => {
        if (profile.accounts['google'] && profile.accounts['google']['email'] && profile.accounts['google']['email'].indexOf('@fbk.eu') > 0) {
          this.navCtrl.setRoot(TabsPage);
        } else {
          const toast = this.toastCtrl.create({
            message: this.translate.instant('toast_account_err'),
            position: 'bottom',
            showCloseButton: true,
            closeButtonText: 'Ok'
          });
          toast.onDidDismiss(() => {
            this.auth.logout();
          });
          toast.present();
        }
      });
    }, err => {
      this.toastCtrl.create({
        message: this.translate.instant('toast_login_err'),
        duration: 5000,
        position: 'bottom'
      }).present();
    });
  }
}
