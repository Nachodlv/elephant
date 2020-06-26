import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class HttpService {

  private DEFAULT_HEADERS = {'Content-Type': 'application/json'}; // in content-role-interceptor

  private readonly baseUrl: string;

  get authToken(): string {
    return localStorage.getItem('user');
  }

  constructor(private http: HttpClient) {
    // const loc = (platformLocation as any).location;
    // this.baseUrl = 'http://' + loc.hostname + ':8080'; // get base url
    this.baseUrl = 'http://localhost:8080';
  }

  get defaultHeaders(): HttpHeaders {
    return new HttpHeaders(this.DEFAULT_HEADERS);
  }

  get defaultOptions(): any {
    return {headers: this.defaultHeaders};
  }

  get defaultHttp(): HttpClient {
    return this.http;
  }

  public get(url: string, options?: any, ignoreBaseUrl?: boolean): Observable<HttpResponse<any>> {
    return this.http.get((ignoreBaseUrl ? '' : this.baseUrl) + url, {headers: this.requestOptions(options), observe: 'response'});
  }

  public getImage(url: string, options?: any, ignoreBaseUrl?: boolean): Observable<any> {
    return this.http.get((ignoreBaseUrl ? '' : this.baseUrl) + url,
      {headers: this.requestOptions(options),
        responseType: 'blob'});
  }

  public post(url: string, body: any, options?: any): Observable<HttpResponse<any>> {
    return (this.http.post(this.baseUrl + url, body, {headers: this.requestOptions(options), observe: 'response'}));
  }

  public put(url: string, body: any, options?: any): Observable<HttpResponse<any>> {
    return (this.http.put(this.baseUrl + url, body, {headers: this.requestOptions(options), observe: 'response'}));
  }

  public delete(url: string, options?: any): Observable<HttpResponse<any>> {
    return (this.http.delete(this.baseUrl + url, {headers: this.requestOptions(options), observe: 'response'}));
  }

  public patch(url: string, body: any, options?: any): Observable<HttpResponse<any>> {
    return (this.http.patch(this.baseUrl + url, body, {headers: this.requestOptions(options), observe: 'response'}));
  }

  public head(url: string, options?: any): Observable<HttpResponse<any>> {
    return (this.http.head(this.baseUrl + url, {headers: this.requestOptions(options), observe: 'response'}));
  }

  public options(url: string, options?: any): Observable<HttpResponse<any>> {
    return (this.http.options(this.baseUrl + url, {headers: this.requestOptions(options), observe: 'response'}));
  }

  private requestOptions(options?: any) {
    const authHeader = {Authorization: `Bearer ${this.authToken}`};
    if (options) {
      return new HttpHeaders(Object.assign(options, authHeader));
    } else {
      return new HttpHeaders(Object.assign(this.DEFAULT_HEADERS, authHeader));
    }
  }
}
