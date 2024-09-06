import { Author } from "../interfaces/AuthorInterface";

const BASE_URL: string = "http://127.0.0.1:5000";       // flask server

export const fetchAuthorDetails = async (authorName: string): Promise<Author | null> => {
    try {
        const response = await fetch(`${BASE_URL}/author?authorName=${authorName}`);
        if (!response.ok) {
            throw new Error(`Response from server failed when fetching details about the author '${authorName}'.`);
        }
        const data = await response.json();
        if (data.message) {
            console.log(data.message);
            return null;
        }
        else {
            const authorDetails: Author = {
                'description': data.description || '',
                'image_url': data.image_url || ''
            }
            return authorDetails;
        }
    }
    catch (error: any) {
        throw error;
    }
}