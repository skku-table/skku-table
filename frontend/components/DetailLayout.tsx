'use client';

import BackButton from '@/components/backButton';
import { ReactNode } from 'react';

type DetailLayoutProps = {
  title: string;
  children: ReactNode;
  footer?: ReactNode;
};

export default function DetailLayout({ title, children, footer }: DetailLayoutProps) {
  return (
    <div className="flex flex-col min-h-screen">
      {/* 상단 고정 헤더 */}
      <header className="fixed top-0 left-0 right-0 bg-white h-14 z-10 flex items-center px-4 text-lg font-bold border-b">
        <BackButton />
        <span className="ml-2">{title}</span>
      </header>

      {/* 본문 내용 */}
      <main className="flex-grow pt-16 p-4 space-y-6">
        {children}
      </main>

      {/* 하단 버튼 (선택) */}
      {footer && (
        <footer className="fixed bottom-0 left-0 right-0 px-4 py-3 bg-white border-t z-10">
          {footer}
        </footer>
      )}
    </div>
  );
}
