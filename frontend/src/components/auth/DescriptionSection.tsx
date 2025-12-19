import steps from '../../assets/steps.svg'

export const DescriptionSection = () => {
    return(
        <section className="bg-primary">
                <div className="text-secondary mx-6.25 mt-10">
                <h1 className="text-5xl text-center font-medium">Grind</h1>
                <p className="text-3xl mt-4">Интеллектуальный трекер личного прогресса.
                    Визуализируй свои достижения, управляй задачами и анализируй результаты в одном месте.</p>
                </div>
                <img className="h-4/5 mx-auto" src={steps}/>
        </section> 
    )
}