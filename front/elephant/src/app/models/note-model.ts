export class Note {
  constructor(
    public uuid?: number,
    public title?: string,
    public content?: string,
    public created?: string,
  ) {
  }

  static fromJson(data: any): Note {
    return Object.assign(new Note(), data);
  }
}
