import { useEffect, useState } from "react";
import { getBookStatusById } from "../services/userBookService";

export const useBookStatus = (bookId: string): [string, (newStatus: string) => void] => {
    const [status, setStatus] = useState<string>(""); // default: unshelved
    const token = sessionStorage.getItem("token");

    useEffect(() => {
        if (!bookId || !token) return;

        const fetchStatus = async () => {
            try {
                const fetchedStatus = await getBookStatusById(bookId);

                // if null then the book was not already shelved
                if (!fetchedStatus) return;

                // convert server enum to readable label
                const readableMap: Record<string, string> = {
                    TO_READ: "Want to read",
                    CURRENTLY_READING: "Currently reading",
                    READ: "Read"
                };

                const readable = readableMap[fetchedStatus];
                if (readable) {
                    setStatus(readable);
                }

            } catch (err) {
                console.error("Unexpected error fetching book status:", err);
            }
        };

        fetchStatus();
    }, [bookId, token]);

    return [status, setStatus];
};
