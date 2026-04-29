import type { AiGenerationHistoryItem } from "@/lib/ai/history/generationHistoryTypes";

export class InMemoryGenerationHistoryStore {
  private readonly items = new Map<string, AiGenerationHistoryItem>();

  save(item: AiGenerationHistoryItem): void {
    this.items.set(item.id, item);
  }

  list(): AiGenerationHistoryItem[] {
    return Array.from(this.items.values()).sort((a, b) => b.createdAt.localeCompare(a.createdAt));
  }

  delete(id: string): boolean {
    return this.items.delete(id);
  }

  clear(): void {
    this.items.clear();
  }
}
