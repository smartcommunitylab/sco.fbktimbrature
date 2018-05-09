import { Injectable } from '@angular/core';
import { Platform } from "ionic-angular";
import { Http, RequestOptions, URLSearchParams, Headers } from '@angular/http';
import { BrowserTab } from '@ionic-native/browser-tab';
import { InAppBrowser } from '@ionic-native/in-app-browser';


import { NetworkInterface } from '@ionic-native/network-interface';
import { Geolocation } from '@ionic-native/geolocation';
import { Diagnostic } from '@ionic-native/diagnostic';

const ERR_LOCATION = 0;
const ERR_NETWORK = 1;
const ERR_MOCK_LOCATION = 2;
const ERR_NETWORK_MISMATCH = 3;
const ERR_LOCATION_MISMATCH = 4;
const ERR_MSG = ['err_check_location', 'err_network', 'err_mock_location', 'err_network_mismatch', 'err_location_mismatch'];

const TARGET_POSITION = [46.067454, 11.150735];
const MAX_DISTANCE = 1;

@Injectable()
export class LocationChecker {
    constructor(public network: NetworkInterface, public geo: Geolocation, public diagnose: Diagnostic) {}

    checkLocation(): Promise<boolean> {
        return new Promise((resolve, reject) => {
          console.log('checking');
          const to = setTimeout(() => {
            reject(ERR_MSG[ERR_LOCATION]);
          }, 20000);
          // check mock location settings disabled
          (<any>window).plugins.mocklocationchecker.check((res) => {
            clearTimeout(to);
            // if disabled, check current location
            if ('true' !== (''+res) && !Array.from(res).some((e:any) => e.info === 'mock-true')) {
              console.log('Mock verified: ok');
              const pos: any = Array.from(res).find((e: any) => e.lat && e.long);
              if (pos) {
                const dist = this.calculateDistance(pos.lat, TARGET_POSITION[0], pos.long, TARGET_POSITION[1]);
                if (dist < MAX_DISTANCE) resolve(true);
                else reject(ERR_MSG[ERR_LOCATION_MISMATCH]);
                return;
              }
              this.geo.getCurrentPosition({timeout: 20000}).then((pos) => {
                console.log(pos);
                const dist = this.calculateDistance(pos.coords.latitude, TARGET_POSITION[0], pos.coords.longitude, TARGET_POSITION[1]);
                if (dist < MAX_DISTANCE) resolve(true);
                else reject(ERR_MSG[ERR_LOCATION_MISMATCH]);
              }).catch((err) => {
                console.error('location err', err);
                reject(ERR_MSG[ERR_LOCATION])
              });
            // if enabled, return err
            } else {
              reject(ERR_MSG[ERR_MOCK_LOCATION]);
            }
          }, (err) => {
            clearTimeout(to);
            // failed to check location settings: report error
            reject(ERR_MSG[ERR_LOCATION]);
          });
        });
      }

      startWatch() {
        this.diagnose.requestLocationAuthorization().then((res) => {
          console.log('geolocation granted: ', res);
        });
        // this.geo.watchPosition().subscribe((pos) => {
        //   console.log(pos);
        // });
      }
      stopWatch() {
        // this.geo.
      }

      private calculateDistance(lat1:number,lat2:number,long1:number,long2:number){
        let p = 0.017453292519943295;    // Math.PI / 180
        let c = Math.cos;
        let a = 0.5 - c((lat1-lat2) * p) / 2 + c(lat2 * p) *c((lat1) * p) * (1 - c(((long1- long2) * p))) / 2;
        let dis = (12742 * Math.asin(Math.sqrt(a))); // 2 * R; R = 6371 km
        return dis;
      }
}