import { Injectable } from '@angular/core';
import { Platform } from "ionic-angular";
import { Http, RequestOptions, URLSearchParams, Headers } from '@angular/http';
import { BrowserTab } from '@ionic-native/browser-tab';
import { InAppBrowser } from '@ionic-native/in-app-browser';

import * as shajs from 'sha.js';

const CHARSET = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
const HAS_CRYPTO = typeof window !== 'undefined' && !!(window.crypto as any);

export class AACAuthConfig {
    constructor(
        public client_id: string,
        public client_secret: string,
        public redirect_uri: string,
        public scopes?: string
    ){}
}

export class AACTokenData {
    constructor(
        public access_token: string,
        public expires_in: number,
        public token_type: string,
        public scope: string,
        public refresh_token?: string,
        public expires_on?: number
    ){}
}

export class BasicProfileData {
    constructor(
        public name: string,
        public surname: string,
        public userId: string
    ) {}
}

export class AccountProfileData extends BasicProfileData {
    constructor(
        public name: string,
        public surname: string,
        public userId: string,
        public accounts: any
    ) {
        super(name, surname, userId);
    }
}
class Deferred<T> {

    promise: Promise<T>;
    resolve: (value?: T | PromiseLike<T>) => void;
    reject:  (reason?: any) => void;
  
    constructor() {
      this.promise = new Promise<T>((resolve, reject) => {
        this.resolve = resolve;
        this.reject  = reject;
      });
    }
  }

export const ERR_MISSING_PROVIDER = 'IncorrectProvier';  
export const ERR_INSUFFICIENT_PERMISSIONS = 'InsufficientPermissions';  
export const ERR_UNAUTHORIZED = 'Unauthorized';  

@Injectable()
export class AACAuth {

    endpoint = 'https://am.smartcommunitylab.it/aac';
    // endpoint = 'http://192.168.31.217:8080/aac';
    readonly TOKEN_URL = '/oauth/token';

    clientConfig: AACAuthConfig;
    deferred: Deferred<AACTokenData>;
    strongAccessToken = null;

    private err(err: any) {
        return {code: err === 'Invalid scope' ? ERR_INSUFFICIENT_PERMISSIONS : ERR_UNAUTHORIZED, err: err};
    }

    constructor(private platform: Platform, private http: Http, private iab: InAppBrowser, private browserTab: BrowserTab) {
        /**
         * Initialize the App Link listener on startup
         */
        this.platform.ready().then(() => {
            /**
             * Process AAC URL and exchange code for token
             * @param url 
             */
            const processAuthCode = (url: string) => {
                console.log("received url: " + url);
                if (url.indexOf('?') < 0) {
                    if (this.deferred) this.deferred.resolve();
                }
                const paramsMap = {};
                url = url.substring(url.indexOf('?')+1);
                if (url.indexOf('#') >= 0) url = url.substring(0, url.indexOf('#'));
                url.substring(url.indexOf('?')+1).split('&').forEach((p) => {
                    const arr = p.split('=');
                    paramsMap[arr[0]] = arr[1];
                });
                const codeParam: string = paramsMap['code'];
                if (codeParam) {
                  if (window['authCode'] && window['authCode'] === codeParam) { return; }
                  window['authCode'] = codeParam;
                  const verifier = window['__auth_verifier'];
                  // exchange code for token
                  this.http.post(`${this.endpoint}${this.TOKEN_URL}`, {}, {params: {
                      client_id: this.clientConfig.client_id,
                      client_secret: this.clientConfig.client_secret,
                      redirect_uri: this.clientConfig.redirect_uri,
                      grant_type: 'authorization_code',
                      code_verifier: verifier,
                      code: codeParam
                  }}).subscribe((res) => {
                      let data: any = res.json();
                      // store token data with refresh token in main storage
                      if (data.refresh_token) {
                        data.expires_on = new Date().getTime() + 1000 *  data.expires_in;
                        window.localStorage._aac_authdata = JSON.stringify(data);
                      // store strong authentication token in memory only  
                      } else if (data.scope && data.scope.split(' ').indexOf('operation.confirmed')) {
                        this.strongAccessToken = data.access_token;
                      }
                      if (this.deferred) {
                          this.deferred.resolve(data);
                      }
                      console.log(data);
                  }, (err) => {
                    console.error('error happened',JSON.stringify(err));
                    if (this.deferred) {
                          this.deferred.reject(this.err(err));
                      }
                  });
                } else if (paramsMap['error']) {
                    this.deferred.reject({code: ERR_UNAUTHORIZED, err: paramsMap['error']});
                } else {
                    if (this.deferred) this.deferred.resolve();
                }
              };
        
              window['handleOpenURL'] = processAuthCode;
        
              if (window['universalLinks']) {
                window['universalLinks'].subscribe('authEvent', function (eventData) {
                    processAuthCode(eventData.url);
                  });            
              }
        });
    }

    /**
     * Configure the component
     * @param config 
     * @param endpoint 
     */
    config(config: AACAuthConfig, endpoint?: string) {
        this.clientConfig = config;
        this.endpoint = endpoint;
    }

    /**
     * Perform login operation with AAC
     * @param provider 
     */
    login(provider: string): Promise<AACTokenData> {
        return this.auth(provider, this.clientConfig).then(tokenData => {
            this.getBasicProfile().then(profile => {
                window.localStorage._aac_profile = JSON.stringify(profile);
            });
            return tokenData;
        });
    }

    /**
     * Return true if the app has already performed succesful login operation with the user
     */
    isLoggedIn(): boolean {
        return !!window.localStorage._aac_authdata;
    }

    /**
     * Logout user from the app
     */
    logout(): Promise<any> {
        window.localStorage._aac_authdata = '';
        window.localStorage._aac_provider = '';
        window.localStorage._aac_profile = '';
        if (this.clientConfig) {
            this.deferred = new Deferred<AACTokenData>();
            const redirect = this.clientConfig.redirect_uri;
            const url = `${this.endpoint}/logout?target=${redirect}`;
            this.browserTab.isAvailable()
            .then((isAvailable: boolean) => {
            //   if (isAvailable) {
            //     this.browserTab.openUrl(url);
            //   } else {
                this.iab.create(url, '_system', 'location=0,hideurlbar=1');
            //   }
            });
            return this.deferred.promise;
    
        }
        return Promise.resolve();
        // TODO Revoke token
    }

    /**
     * Obtain strong access token for the current configuration
     */
    getStrongToken(): Promise<AACTokenData> {
        const provider = window.localStorage._aac_provider;
        const config: AACAuthConfig = {
            client_id: this.clientConfig.client_id,
            client_secret: this.clientConfig.client_secret,
            redirect_uri: this.clientConfig.redirect_uri,
            scopes: (this.clientConfig.scopes || '') + ' operation.confirmed'
        }
        return this.auth(provider, config, true);
    }

    /**
     * get an existing or a new access token (using Refresh token if available)
     */
    getAccessToken(): Promise<AACTokenData> {
        const tokenData = JSON.parse(window.localStorage._aac_authdata || {});
        if (tokenData && tokenData.access_token && !this.isExpired(tokenData)) {
            return Promise.resolve(tokenData);
        }

        this.deferred = new Deferred<AACTokenData>();
        if (tokenData && tokenData.refresh_token) {
            this.http.post(`${this.endpoint}${this.TOKEN_URL}`, {}, {params: {
                client_id: this.clientConfig.client_id,
                client_secret: this.clientConfig.client_secret,
                redirect_uri: this.clientConfig.redirect_uri,
                grant_type: 'refresh_token',
                refresh_token: tokenData.refresh_token
            }}).subscribe((res) => {
                let data: any = res.json();
                // store token data with refresh token in main storage
                if (data.refresh_token) {
                    data.expires_on = new Date().getTime() + 1000 *  data.expires_in;
                    window.localStorage._aac_authdata = JSON.stringify(data);
                    // store strong authentication token in memory only  
                } else if (data.scope && data.scope.split(' ').indexOf('operation.confirmed')) {
                    this.strongAccessToken = data.access_token;
                }
                if (this.deferred) {
                    this.deferred.resolve(data);
                }
                console.log(data);
            }, (err) => {
                this.auth(window.localStorage._aac_provider, this.clientConfig).then((data) => {
                    if (this.deferred) {
                        this.deferred.resolve(data);
                    }
                }, err => this.deferred.reject(this.err(err)));
            });
        } else {
            this.auth(window.localStorage._aac_provider, this.clientConfig).then((data) => {
                if (this.deferred) {
                    this.deferred.resolve(data);
                }
            }, err => this.deferred.reject(this.err(err)));
        }
        return this.deferred.promise;
    }

    getBasicProfile(): Promise<BasicProfileData> {
        return new Promise((resolve, reject) => {
            this.getAccessToken().then(tokenData => {
                const options = new RequestOptions();
                options.headers = new Headers();
                options.headers.append('Authorization', `Bearer ${tokenData.access_token}`);
                this.http.get(`${this.endpoint}/basicprofile/me`, options)
                .subscribe(res => resolve(res.json()), err => reject(this.err(err)));
            }, err => reject(this.err(err)));
        });
    }

    getStoredBasicProfile(): BasicProfileData {
        return JSON.parse(window.localStorage._aac_profile || '{}');
    }

    getAccountProfile(): Promise<AccountProfileData> {
        return new Promise((resolve, reject) => {
            this.getAccessToken().then(tokenData => {
                const options = new RequestOptions();
                options.headers = new Headers();
                options.headers.append('Authorization', `Bearer ${tokenData.access_token}`);
                    this.http.get(`${this.endpoint}/accountprofile/me`, options)
                .subscribe(res => resolve(res.json()), err => reject(this.err(err)));
            }, err => reject(this.err(err)));
        });
    }

    private isExpired(data: AACTokenData): boolean {
        // give margin of 1 hour
        if (data.expires_on && data.expires_on < new Date().getTime() - 1000*60*60) {
            return true;
        }
        return false;
    }

    private auth(provider: string, config: AACAuthConfig, inExtBrowser?: boolean): Promise<AACTokenData> {
        if (!provider) return Promise.reject({code: ERR_MISSING_PROVIDER});

        this.deferred = new Deferred<AACTokenData>();

        if (!config) {
            this.deferred.reject('Missing configuration');
        }
        const verifier = this.createVerifier();
        const challenge = this.createChallenge(verifier);
        window['__auth_verifier'] = verifier;
        let url = `${this.endpoint}/eauth/authorize/${provider}?client_id=${config.client_id}&code_challenge=${challenge}&code_challenge_method=S256&grant_type=authorization_code&${(config.scopes ? ('scope='+config.scopes) : '')}&response_type=code&redirect_uri=${config.redirect_uri}`;

        // store provide for future usage
        window.localStorage._aac_provider = provider;
    
        this.browserTab.isAvailable()
        .then((isAvailable: boolean) => {
          if (isAvailable && !inExtBrowser) {
            this.browserTab.openUrl(url);
          } else {
            this.iab.create(url, '_system', 'location=0,hideurlbar=1');
          }
        });
        return this.deferred.promise;

    }

    private createVerifier(): string {
        return this.cryptoGenerateRandom(32);
    }
    private createChallenge(verifier: string): string {
        return this.base64URLEncode(shajs('sha256').update(verifier).digest('base64'));
    }

    private base64URLEncode(str: string) {
        return str
            .replace(/\+/g, '-')
            .replace(/\//g, '_')
            .replace(/=/g, '');
    }
    private bufferToString(buffer: Uint8Array) {
        let state = [];
        for (let i = 0; i < buffer.byteLength; i += 1) {
            let index = (buffer[i] % CHARSET.length) | 0;
            state.push(CHARSET[index]);
        }
        return state.join('');
    }
    private cryptoGenerateRandom(sizeInBytes = 1) {
        const buffer = new Uint8Array(sizeInBytes);
        if (HAS_CRYPTO) {
            window.crypto.getRandomValues(buffer);
        } else {
            // fall back to Math.random() if nothing else is available
            for (let i = 0; i < sizeInBytes; i += 1) {
            buffer[i] = Math.random();
            }
        }
        return this.bufferToString(buffer);
    };
}