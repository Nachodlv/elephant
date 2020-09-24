export class Comment {
  constructor(
    public uuid?: number,
    public title?: string,
    public content?: string,
    public owner?: string,
    public created?: string,
  ) {
  }

  static fromJson(data: any): Comment {
    return Object.assign(new Comment(), data);
  }
}