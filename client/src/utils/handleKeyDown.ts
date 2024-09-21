export const handleKeyDown = (event: React.KeyboardEvent<HTMLInputElement>, functionToCall: () => void) => {
    if (event.key === 'Enter') {
        functionToCall();
    }
}
