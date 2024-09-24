export const sliceDescription = (description: string): string => {
    // Split the description into words 
    const words: Array<string> = description.split(/\s+/);

    // Get the first 60 words
    const slicedDescription = words.slice(0, 60);

    return slicedDescription.join(' ');
}

export const sliceDescriptionBySentences = (description: string, startIndex: number, numberOfSentences: number): string => {
    // Split the description into sentences using a regex
    const sentences: Array<string> = description.match(/[^.!?]+[.!?]+/g) || [];

    // Ensure we don't go out of bounds when slicing
    const endIndex = Math.min(startIndex + numberOfSentences, sentences.length);

    // Get the desired number of sentences from the start index
    const slicedDescription = sentences.slice(startIndex, endIndex);

    // Join the sentences back together and return the result
    return slicedDescription.join(' ').trim();
};
