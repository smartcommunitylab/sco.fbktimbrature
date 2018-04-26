import { Component } from '@angular/core';
import { NavController, App } from 'ionic-angular';
import { AACAuth } from '../../services/aac';
import { LoginPage } from '../login/login';
import { AttendanceService } from '../../services/attendance';

import * as moment from 'moment';

@Component({
  selector: 'page-settings',
  templateUrl: 'settings.html'
})
export class SettingsPage {

  pattern: string;
  notificationsEnabled = false;
  skipToday = false;

  constructor(public appCtrl: App, public auth: AACAuth, public navCtrl: NavController, public aac: AACAuth, public attendanceService: AttendanceService) {
    this.pattern = this.attendanceService.getCurrentSchedule();
    if (!this.pattern) {
      this.notificationsEnabled = false;
      this.pattern = '17:00';
    } else {
      this.notificationsEnabled = true;
    } 
  }

  ionViewDidEnter() {
    this.auth.getAccessToken().then((tokenData) => {
      this.attendanceService.getTodayRecords(tokenData.access_token).subscribe((data) => {
        if (data && data.length > 0) {
          this.skipToday = true;
        }
      });
    });
  }

  toggleNotifications() {
    if (!this.notificationsEnabled) {
      this.attendanceService.clearSchedule();
    } else {
      const m = moment(this.pattern,'HH:mm');
      this.attendanceService.scheduleAt(m.hours(), m.minutes(), this.skipToday ? moment().startOf('day').add(1,'days').toDate() : null);
    }
  }

  updateNotificationsTime() {
    const m = moment(this.pattern,'HH:mm');
    this.attendanceService.scheduleAt(m.hours(), m.minutes(), this.skipToday ? moment().startOf('day').add(1,'days').toDate() : null);  
  }

  logout() {
    this.aac.logout().then(() => {
      this.appCtrl.getRootNav().setRoot(LoginPage);
    });
  }

}
