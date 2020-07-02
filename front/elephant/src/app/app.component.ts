import { Component } from '@angular/core';
import {MessageService} from 'primeng/api';
import {CommonMessage, ElephantMessage, MessageSeverity} from './models/elephant-message.model';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'elephant';

  constructor(private messageService: MessageService) {
  }

  showMessage(): void {
    this.messageService.add(new ElephantMessage(MessageSeverity.SUCC, CommonMessage.SUCC_TITLE, 'Hello World!'));
  }
}
