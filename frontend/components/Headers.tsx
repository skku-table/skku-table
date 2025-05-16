import BackButton from '@/components/backButton'

type headerType={
    isBackButton: boolean,
    title: string
}

export default function Header({isBackButton, title}: headerType){
    return(
        <div className="flex align-middle text-xl font-bold bg-white w-full h-14 z-10 fixed">
            {isBackButton? <BackButton/> : <p className='w-5'>&nbsp;</p>}
            <p className='content-center'>
                {title}
            </p>
        </div>
    )
}