import { CircleChevronDown } from 'lucide-react'
import { SideBar } from '../components/home/SideBar'
import { CheckBox } from '../components/ui/CheckBox'

export const Home = () => {
    return(
        <>
        <header className="font-jetbrains flex bg-primary h-15 items-center">
            <h1 className="text-secondary text-4xl mx-6.25">Grind</h1>
        </header>
        <main className="font-jetbrains flex h-dvh">
            <SideBar/>
            <section className="bg-secondary w-full">
                <div className="p-2.5">
                    <div className="flex bg-white rounded-lg h-11.5 items-center justify-between">
                        <div className="flex ml-3 gap-2">
                            <CheckBox/>
                            <div className="bg-secondary-dark w-px"></div>
                            <p>Задача 1</p>
                        </div>
                        <CircleChevronDown className="text-primary-dark mr-3"/>
                    </div>
                </div>
            </section>
        </main>
        </>
    )
}
