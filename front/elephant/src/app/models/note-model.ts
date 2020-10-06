export class Note {
  constructor(
    public uuid?: number,
    public title?: string,
    public content?: string,
    public created?: string,
    public tags?: string[],
  ) {
  }

  static fromJson(data: any): Note {
    return Object.assign(new Note(), data);
  }
}

export class Tags {
  constructor(
    public tags: string[],
  ) {
  }
}
