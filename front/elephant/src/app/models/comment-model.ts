export class Comment {
  constructor(
    public content?: string,
    public uuid?: number,
    public title?: string,
    public owner?: string,
    public created?: string,
  ) {
  }

  static fromJson(data: any): Comment {
    return Object.assign(new Comment(), data);
  }
}
