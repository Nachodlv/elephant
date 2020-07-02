import { Pipe, PipeTransform } from '@angular/core';

@Pipe({name: 'example'})
export class ExamplePipe implements PipeTransform {
  transform(value: number): string {
    if (value > 0) return `${Math.floor(value / 60)}:${('0' + value % 60).slice(-2)}`;
    else return '00:00';
  }
}
