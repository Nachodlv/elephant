export class User {
  constructor(
    public firstName?: string,
    public lastName?: string,
    public email?: string,
    public password?: string,
  ) {
  }

  static fromJson(data: any): User {
    return Object.assign(new User(), data);
  }
  updateName(newFirstName: string, newLastName: string): void{
  this.firstName = newFirstName;
  this.lastName = newLastName;
  }
}
