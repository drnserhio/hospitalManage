export class RequestTable {
  column: string;
  sort: string;
  page: number;
  size: number;


  constructor(column: string,
              sort: string,
              page: number,
              size: number) {
    this.column = column;
    this.sort = sort;
    this.page = page;
    this.size = size;
  }
}
