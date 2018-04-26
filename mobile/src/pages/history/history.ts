import { Component } from '@angular/core';
import { NavController, ToastController, LoadingController, InfiniteScroll } from 'ionic-angular';
import { AACAuth } from '../../services/aac';
import { AttendanceService, AttendanceRecord } from '../../services/attendance';
import { TranslateService } from '@ngx-translate/core';

import * as moment from 'moment';

@Component({
  selector: 'page-history',
  templateUrl: 'history.html'
})
export class HistoryPage {

  from: number;
  records: AttendanceRecord[] = null;
  groups: any[] = null;

  constructor(public navCtrl: NavController, 
    public auth: AACAuth, public attendanceService: AttendanceService,
    public translate: TranslateService, 
    public toastCtrl: ToastController, public loadingCtrl: LoadingController) {}

  ionViewDidLoad(){
    const toMoment = moment().startOf('day');
    const to = toMoment.toDate().getTime();
    this.from = toMoment.subtract(7, 'days').toDate().getTime();
    this.update(this.from, to);
  }
  

  update(from: number, to: number, infiniteScroll?: InfiniteScroll) {
    this.auth.getAccessToken().then((tokenData) => {
      const loading = this.loadingCtrl.create();
      if (!infiniteScroll) loading.present();  
      this.attendanceService.getRecords(tokenData.access_token, from, to).subscribe(res => {
        if (!this.records) this.records = [];
        this.records = this.records.concat(res);
        this.groupList(res, from, to);
        if (!infiniteScroll) loading.dismiss();
        if (infiniteScroll) infiniteScroll.complete();
      }, (err) => {
        if (!infiniteScroll) loading.dismiss();
        this.toastCtrl.create({
          message: this.translate.instant('toast_server_err'),
          duration: 3000,
          position: 'bottom'
        }).present();
        if (infiniteScroll) infiniteScroll.complete();
      });
    });
  }

  groupList(list: AttendanceRecord[], from: number, to: number) {
    const map = {};
    let fm = moment(from);
    let tm = moment(to);
    list.forEach(r => {
      const day = moment(r.timestamp).format('DD/MM/YYYY');
      if (!map[day]) map[day] = [];
      map[day].push(r);
    });

    const res = [];
    while(fm.isBefore(tm)) {
      const str = tm.format('DD/MM/YYYY');
      if (tm.day() > 0 && tm.day() < 6) {
        res.push({date: str, dayRecords: map[str]});
      }
      tm = tm.subtract(1, 'day');
    }
    if (!this.groups) this.groups = res;
    else this.groups = this.groups.concat(res);
  }

  doInfinite(infiniteScroll) {
    console.log('Begin async operation');

    const fm = moment(this.from).subtract(7, 'days').toDate().getTime();
    this.update(fm, this.from, infiniteScroll);
    this.from = fm;
  }
}
