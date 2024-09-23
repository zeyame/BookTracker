import { book } from "./BookInterface";

export interface BookWithStatus {
    bookData: book, 
    status: string
}