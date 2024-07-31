const BASE_URL: string = "http://127.0.0.1:5000";       // flask server

export const fetchAuthorDescription = async (authorName: string) => {
    try {
        const response = await fetch(`${BASE_URL}/author?authorName=${authorName}`);
        if (!response.ok) {
            throw new Error(`Response from server failed when fetching details about the author '${authorName}'.`);
        }
        const data = await response.json();
        const result: string = data.message ? data.message : data.description;
        return result;
    }
    catch (error: any) {
        throw error;
    }
}