type Edition = {
    publisher: string
    publishedDate: string
}

export interface book {
    id: string
    title: string
    authors: Array<string>
    publisher: string
    publishedDate?: string
    description: string
    pageCount: number
    categories: Array<string>
    image_url: string
    language: string
    editions?: Array<Edition> 
}