export class ResponseTable<T> {

  allItemsSize: number;
  page: number;
  totalPages: number;
  size: number;
  content: T[];
  sort: string;
  columnSort: string;

}
