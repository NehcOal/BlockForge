import type { Metadata } from "next";
import "./globals.css";

export const metadata: Metadata = {
  title: "BlockForge",
  description: "Generate Minecraft-style voxel buildings from text prompts."
};

export default function RootLayout({
  children
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en">
      <body>{children}</body>
    </html>
  );
}
