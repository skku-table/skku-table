//하단배너
// app/(client)/layout.tsx

'use client'
import Link from "next/link";
import { usePathname } from "next/navigation";
import { ReactNode } from "react";
import { GrHomeRounded } from "react-icons/gr";
import { HiOutlineUserCircle } from "react-icons/hi2";
import { HiUserCircle } from "react-icons/hi2";
import { IoPeopleOutline } from "react-icons/io5";
import { IoPeople } from "react-icons/io5";
import { RiPagesLine } from "react-icons/ri";
import { RiPagesFill } from "react-icons/ri";






import cn from 'classnames'

export default function ClientLayout({ children }: { children: ReactNode }) {
  const pathname=usePathname()
  const isCurrentTab=(tab:string)=>{
    if (tab === ''){
      return pathname === '/admin'
    }
    return pathname.startsWith(`/admin/${tab}`)
  }

  return (
    <div className="flex flex-col min-h-screen relative">
      <main className="flex-grow mb-16">{children}</main>

      {/* 하단 네비게이션 */}
      <nav className="h-16 bottom-0 w-full bg-white flex justify-around items-center shadow-2xl fixed">
        <Link href="/admin">
          <GrHomeRounded className={cn('h-10 w-10 p-1',isCurrentTab('') && 'fill-black')}/>
        </Link>
        <Link href="/admin/checkReservation">
          {isCurrentTab('checkReservation')? <IoPeople className="h-10 w-10 stroke-1"/> : <IoPeopleOutline className="h-10 w-10 stroke-1"/>}
        </Link>
        <Link href="/admin/manageBooth">
          {isCurrentTab('manageBooth')? <RiPagesFill className="h-10 w-10 p-0.5"/> : <RiPagesLine className="h-10 w-10 p-0.5"/>}
        </Link>
        <Link href="/admin/myprofile">
          {isCurrentTab('myprofile')? <HiUserCircle className="h-10 w-10"/> : <HiOutlineUserCircle className="h-10 w-10"/>}
        </Link>
      </nav>
    </div>
  );
}