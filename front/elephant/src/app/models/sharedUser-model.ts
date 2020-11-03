export class SharedUser {
  constructor(
    public email?: string,
    public type?: string
  ) {
  }

  static fromJson(data: any): Comment {
    return Object.assign(new SharedUser(), data);
  }
}
