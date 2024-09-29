import { BASE_URL } from "../global-variables/BaseUrl";
import { Author } from "../interfaces/AuthorInterface";

export const fetchAuthorDetails = async (authorName: string): Promise<Author | null> => {
    try {
        const response = await fetch(`${BASE_URL}/api/authors?authorName=${authorName}`);
        if (!response.ok) {
            throw new Error(`Response from server failed when fetching details about the author '${authorName}'.`);
        }
        const data = await response.json();
        const authorData = data.authorDetails;
        
        const authorDetails: Author = {
            'description': authorData.description || '',
            'imageUrl': authorData.imageUrl || ''
        }
    
        return authorDetails;
        
    }
    catch (error: any) {
        throw error;
    }
}