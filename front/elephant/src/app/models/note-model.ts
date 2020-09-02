export class Note {
  constructor(
    public uuid?: string,
    public title?: string,
    public content?: string,
    public created?: string,
  ) {
  }

  static fromJson(data: any): Note {
    return Object.assign(new Note(), data);
  }
}
