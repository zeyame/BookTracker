export const sliceDescription = (description: string): string => {
    // Split the description into words 
    const words: Array<string> = description.split(/\s+/);

    // Get the first 100 words
    const slicedDescription = words.slice(0, 60);

    return slicedDescription.join(' ');
}