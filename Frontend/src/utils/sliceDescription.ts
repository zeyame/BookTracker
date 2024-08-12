export const sliceDescription = (description: string): string => {
    // Split the description into sentences using regex to account for various sentence endings
    const sentences = description.split(/(?<=[.!?])\s+/);
        
    // Slice the array to get the first 5 sentences
    const slicedSentences = sentences.slice(0, 5);

    // Join the sentences back into a single string
    return slicedSentences.join(' ');
}