export class BlockForgeStorageError extends Error {
  constructor(message: string) {
    super(message);
    this.name = "BlockForgeStorageError";
  }
}
