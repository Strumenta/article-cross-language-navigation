export function isOSWindows(): boolean {
    return process.platform === "win32";
}

export function isOSnix(): boolean {
    let platform = process.platform;
    return platform === "linux"
        || platform === "darwin"
        || platform === "freebsd"
        || platform === "openbsd";
}