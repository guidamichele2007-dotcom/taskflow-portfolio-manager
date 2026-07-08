import type { Metadata } from "next";
import "./globals.css";

export const metadata: Metadata = {
  title: "Shipfolio — Your GitHub, turned into a portfolio that gets you hired",
  description:
    "Shipfolio reads your GitHub repos and writes recruiter-ready case studies with AI, then publishes them as a portfolio site in minutes.",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en" className="h-full antialiased">
      <body className="min-h-full flex flex-col">{children}</body>
    </html>
  );
}
