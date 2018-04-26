import { Component } from '@angular/core';

import { HomePage } from '../home/home';
import { SettingsPage } from '../settings/settings';
import { HistoryPage } from '../history/history';
import { TranslateService } from '@ngx-translate/core';

@Component({
  templateUrl: 'tabs.html'
})
export class TabsPage {

  tab1Root = HomePage;
  tab2Root = HistoryPage;
  tab3Root = SettingsPage;

  tab1Title = '';
  tab2Title = '';
  tab3Title = '';

  constructor(private translate: TranslateService) {
    this.tab1Title = translate.instant('tab_home');
    this.tab2Title = translate.instant('tab_history');
    this.tab3Title = translate.instant('tab_settings');
  }
}
