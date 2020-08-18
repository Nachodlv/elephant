import {ErrorHandler, Injectable, Injector} from '@angular/core';

@Injectable()
export class ErrorService implements ErrorHandler {


  constructor(private injector: Injector) {
  }

  handleError(error: Error): void {
    // const messageService = this.injector.get(MessageService);

    let message;
    // let stackTrace;

    // Client Error
    message = this.getClientMessage(error);
    // stackTrace = this.getClientStack(error);
    // messageService.add(new ElephantMessage(MessageSeverity.ERROR, CommonMessage.ERROR_TITLE, message));

    // Always log errors
    // logger.logError(message, stackTrace);

    console.error(error);
  }

  private getClientMessage(error: Error): string {
    if (!navigator.onLine) {
      return 'No internet connection';
    }
    return error.message ? error.message : error.toString();
  }
}
