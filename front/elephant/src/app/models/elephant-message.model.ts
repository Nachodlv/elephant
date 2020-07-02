import {Message} from 'primeng/api';

export class ElephantMessage implements Message {

  constructor(
    public severity?: string,
    public summary?: string,
    public detail?: string,
    public life?: number
  ) {

  }
}

export enum MessageSeverity {
  SUCC = 'success',
  INFO = 'info',
  WARN = 'warn',
  ERROR = 'error'
}

export enum CommonMessage {
  ERROR = 'An error has occurred. Please try again later...',
  ERROR_TITLE = 'Error',
  SUCC_TITLE = 'Success',
  WARN_TITLE = 'Warning',
  INFO_TITLE = 'Info'
}
