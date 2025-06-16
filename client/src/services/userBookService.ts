import { BASE_URL } from "../global-variables/BaseUrl";
import { book } from "../interfaces/BookInterface";
import { authorizedFetch } from "../utils/authorizedFetch";

const readingStatusToBackendEnum: Record<string, string> = {
    "Want to read": "TO_READ",
    "Currently reading": "CURRENTLY_READING",
    "Read": "READ"
};

export const getBookStatusById = async (bookId: string): Promise<string | null> => {
    try {
        const response = await authorizedFetch(`${BASE_URL}/api/user/books/${bookId}/status`, {
            method: "GET"
        });

        if (response.status === 404) {
            return null;
        }

        if (!response.ok) {
            throw new Error("Failed to fetch book status.");
        }

        const data = await response.json();
        return data.status as string; // e.g. TO_READ, READ, CURRENTLY_READING
    } catch (error : any) {
        throw error;
    }
};


export const getUserBooksByStatus = async (status: string) : Promise<Array<book>> => {
    try {
        const enumStatus = readingStatusToBackendEnum[status];
        const response = await authorizedFetch(`${BASE_URL}/api/user/books?status=${enumStatus}`, {
            method: 'GET',
        });
        if (!response.ok) {
            throw new Error("An unexpected error occurred when fetching your stored books. Please try again.");
        }

        const data = await response.json();
        return data.books as Array<book>;
    } catch (error : any) {
        throw error;
    }
}

export const doesUserHaveBook = async (bookId: string): Promise<boolean> => {

    try {
        const response = await authorizedFetch(`${BASE_URL}/api/user/books/${bookId}/status`, {
            method: "GET",
        });

        if (response.status === 404) return false;
        if (!response.ok) throw new Error("Failed to check if book exists");

        return true;   
    } catch (error: any) {
        throw error;
    }
};


export const addUserBookByStatus = async (book: book, bookStatus: string, authorDescription? : string) : Promise<void> => {
    try {
        const enumStatus = readingStatusToBackendEnum[bookStatus];
        const response = await authorizedFetch(`${BASE_URL}/api/user/books`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            }, 
            body: JSON.stringify({
                bookData: book,
                status: enumStatus,
                authorDescription: authorDescription
            })
        });

        if (!response.ok) {
            throw new Error(`Failed to add book to your '${bookStatus}' list. Please try again`);
        }
    } catch (error: any) {
        throw error;   
    }
}

export const updateUserBookStatus = async (bookId: string, newStatus: string): Promise<void> => {
    try {
        const enumStatus = readingStatusToBackendEnum[newStatus];

        const response = await authorizedFetch(`${BASE_URL}/api/user/books`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ 
                bookId: bookId,
                status: enumStatus 
            })
        });

        if (!response.ok) {
            throw new Error("Failed to update book status");
        }
    } catch (error: any) {
        throw error;
    }
};

export const removeUserBook = async (bookId: string): Promise<void> => {
    try {
        const response = await authorizedFetch(`${BASE_URL}/api/user/books/${bookId}`, {
            method: 'DELETE'
        });

        if (!response.ok) {
            throw new Error("Failed to remove book from shelf.");
        }
    } catch (error : any) {
        throw error;
    }
};
