export class User {
  constructor(
    public uuid: number,
    public firstName: string,
    public lastName: string,
    public email: string,
    public password: string,
  ) {
  }
}
