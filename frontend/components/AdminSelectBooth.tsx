'use client'

import {
    Select,
    SelectContent,
    SelectItem,
    SelectTrigger,
    SelectValue,
  } from "@/components/ui/select"
  import { useRouter } from "next/navigation";

  type MyBoothdata = {
    id: number;
    festivalId: number;
    festivalName: string;
    name: string;
    host: string;
    location: string;
    description: string;
    startDateTime: string;
    endDateTime: string;
    likeCount: number;
    posterImageUrl: string;
    eventImageUrl: string;
  }

  export default function AdminSelectBooth({boothsdata, boothname}:{boothsdata:MyBoothdata[] , boothname:string}) {
    const router = useRouter();

    const handleValueChange = (selectedBoothName: string) => {
        // 선택된 name을 통해 해당 booth 객체를 찾습니다.
        const selectedBooth = boothsdata.find(booth => booth.name === selectedBoothName);
        if (selectedBooth) {
            router.push(`/admin/checkReservation/${selectedBooth.festivalId}/${selectedBooth.id}`);
        }
    };

    return(
        <>
        <Select onValueChange={handleValueChange}> {/* 여기에 onValueChange를 사용합니다. */}
                <SelectTrigger className="w-[180px]">
                    <SelectValue placeholder={boothname}/>
                </SelectTrigger>
                <SelectContent>
                    {boothsdata.map((booth) => (
                        <SelectItem key={booth.id} value={booth.name}> {/* onClick 대신 value만 사용합니다. */}
                            {booth.name}
                        </SelectItem>
                    ))}
                </SelectContent>
            </Select>
        </>
    )
  }