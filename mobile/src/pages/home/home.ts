import { Component } from '@angular/core';
import { NavController, Platform, ToastController, App, LoadingController, ModalController, NavParams, ViewController } from 'ionic-angular';
import { AACAuth, ERR_MISSING_PROVIDER, BasicProfileData } from '../../services/aac';
import { LoginPage } from '../login/login';
import { TranslateService } from '@ngx-translate/core';
import { LocationChecker } from '../../services/location';
import { AttendanceService, AttendanceRecord } from '../../services/attendance';

@Component({
  selector: 'page-home',
  templateUrl: 'home.html'
})
export class HomePage {
  now: Date;
  records: AttendanceRecord[] = null;
  profile: BasicProfileData;

  constructor(
    public appCtrl: App, public navCtrl: NavController, 
    public auth: AACAuth, public location: LocationChecker, public attendanceService: AttendanceService,
    private platform: Platform, public translate: TranslateService, 
    public toastCtrl: ToastController, public loadingCtrl: LoadingController,
    public modalCtrl: ModalController) {
  }

  ionViewDidLoad(){
    this.now = new Date();
    this.update();
    this.profile = this.auth.getStoredBasicProfile();
    setInterval(()=> this.now = new Date(), 60000);
  }

  update() {
    this.auth.getAccessToken().then((tokenData) => {
      const loading = this.loadingCtrl.create();
      loading.present();  
      this.attendanceService.getTodayRecords(tokenData.access_token).subscribe(res => {
        this.records = res;
        loading.dismiss();
      }, (err) => {
        loading.dismiss();
        this.records = [];
        this.toastCtrl.create({
          message: this.translate.instant('toast_server_err'),
          duration: 3000,
          position: 'bottom'
        }).present();
      });
    });
  }

  register() {
    // this.showRatingModal();
    // return;

    let loading = this.loadingCtrl.create();
    loading.present();
    this.location.checkLocation()
    .then(res => {
      if (res) {
        loading.dismiss();
        this.auth.getStrongToken().then(tokenData => {
          loading = this.loadingCtrl.create();
          loading.present();
          this.attendanceService.registerAttendance(tokenData.access_token).subscribe((res) => {
            loading.dismiss();
            setTimeout(() => {
              this.showRatingModal().then(() => {
                this.update();
              });  
            });
          }, err => {
            loading.dismiss();
            this.toastCtrl.create({
              message: this.translate.instant('toast_server_err'),
              duration: 3000,
              position: 'bottom'
            }).present();    
          });
        }).catch(err => {
          loading.dismiss();
          if (err.code === ERR_MISSING_PROVIDER) {
            this.auth.logout().then(() => this.appCtrl.getRootNav().setRoot(LoginPage));
          } else {
            loading.dismiss();
            this.toastCtrl.create({
              message: this.translate.instant('toast_strong_err'),
              duration: 5000,
              position: 'bottom'
            }).present();
          }
        });
      } else {
        loading.dismiss();
        this.toastCtrl.create({
          message: this.translate.instant('err_mock_location'),
          duration: 5000,
          position: 'bottom'
        }).present();
      }
    })
    .catch(err => {
      loading.dismiss();
      this.toastCtrl.create({
        message: this.translate.instant(err),
        duration: 5000,
        position: 'bottom'
      }).present();
    });
  }

  showRatingModal(): Promise<any> {
    return this.modalCtrl.create(RatingModal, {}, {cssClass: 'rating-popup'}).present();
  }
}

@Component({
  selector: 'rating-modal',
  templateUrl: 'rating-modal.html'
})
export class RatingModal {

 constructor(params: NavParams, public viewCtrl: ViewController, public aac: AACAuth, public attendance: AttendanceService) {
 }

 onModelChange(event) {
   this.aac.getAccessToken().then(tokenData => {
     this.attendance.rate(event, tokenData.access_token);
     this.viewCtrl.dismiss();
   }); 
 }
}
