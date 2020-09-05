export class Note {
  uuid: number;
  title: string;
  content: string;
  created: string;

  constructor(id?: number, name?: string) {
    this.uuid = id;
    this.title = name;
  }
  static fromJson(data: any): Note {
    return Object.assign(new Note(), data);
  }

}
