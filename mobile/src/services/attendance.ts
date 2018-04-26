import { Injectable } from '@angular/core';
import { Platform } from "ionic-angular";
import { Http, RequestOptions, URLSearchParams, Headers } from '@angular/http';
import { BrowserTab } from '@ionic-native/browser-tab';
import { IfObservable } from 'rxjs/observable/IfObservable';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/map';
import { TranslateService } from '@ngx-translate/core';

import * as moment from 'moment';

export class AttendanceRecord {
  constructor(
    public id: number,
    public account: string,
    public timestamp: string
  ) {

  }
}

@Injectable()
export class AttendanceService {
    readonly endpoint = 'https://attendance.fbk.eu/api/attendance';

    localNotifications = window['cordova'].plugins.notification.local;

    constructor(public http: Http, public translate: TranslateService) {}

    registerAttendance(token: string): Observable<AttendanceRecord> {
      const options = new RequestOptions();
      options.headers = new Headers();
      options.headers.append('Authorization', `Bearer ${token}`);
      return this.http.post(`${this.endpoint}`, {}, options).map(res => {
        this.updateSchedule();
        return res.json()
      });
    }

    getTodayRecords(token: string): Observable<AttendanceRecord[]> {
      const options = new RequestOptions();
      options.headers = new Headers();
      options.headers.append('Authorization', `Bearer ${token}`);
      const from = moment().startOf('day').toDate().toISOString();
      return this.http.get(`${this.endpoint}?fromTs=${from}&size=100`, options).map(res => res.json().content);
    } 

    getRecords(token: string, from: number = 0, to: number = new Date().getTime()): Observable<AttendanceRecord[]> {
      const options = new RequestOptions();
      options.headers = new Headers();
      options.headers.append('Authorization', `Bearer ${token}`);
      const fromStr = new Date(from).toISOString();
      const toStr = new Date(to).toISOString();
      return this.http.get(`${this.endpoint}?fromTs=${fromStr}&toTs=${toStr}&size=200`, options).map(res => {
        return res.json().content
        // const arr = [];
        // let fm = moment(from);
        // let tm = moment(to);
        // while (fm.isBefore(tm)) {
        //   for (let i = 0; i < 5; i ++) {
        //     const item = {id: i, timestamp: tm.toDate().toISOString()};
        //     arr.push(item);
        //   }
        //   tm = tm.subtract(1, 'day');
        // }
        // return arr;
      });
    } 

    getCurrentSchedule(): string {
      return window.localStorage.schedule;
    }

    scheduleAt(hour: number, minute: number, from?: Date) {
      this.localNotifications.clearAll();
      const notifications = [];
      let ms = from ? moment(from) : moment();
      ms.startOf('day');
      ms.hours(hour);
      ms.minutes(minute);
      let i = 0;
      while (notifications.length < 30) {
        if (ms.day() < 6 && ms.day() > 0) {
          notifications.push({
            id: i+1,
            title: this.translate.instant('notification_title'),
            text: this.translate.instant('notification_text'),
            trigger: {at: ms.toDate()}
          });
        }
        ms.add(1, 'days');
        i++;
      }
      this.localNotifications.schedule(notifications);
      window.localStorage.schedule = ms.format('HH:mm');
    }

    private updateSchedule() {
      if (!!window.localStorage.schedule) {
        const pattern = window.localStorage.schedule;
        const parsed = moment(pattern, 'HH:mm');
        this.scheduleAt(parsed.hours(), parsed.minutes(), moment().endOf('day').toDate());
      } 
    }

    clearSchedule() {
      window.localStorage.schedule = '';
      this.localNotifications.clearAll();
    }
}