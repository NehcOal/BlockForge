import { NextResponse } from "next/server";
import { getAiStatus } from "@/lib/ai";

export function GET() {
  return NextResponse.json(getAiStatus());
}
