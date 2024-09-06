export interface book {
    id: string
    title: string
    authors: Array<string>
    publisher: string
    publishedDate?: string
    description: string
    pageCount: number
    categories: Array<string>
    imageUrl: string
    language: string
}