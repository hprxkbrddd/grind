import { useState } from 'react';
import { SideBarButton } from '../ui/SideBarButton';

export const SideBar = () => {
  const [activeIndex, setActiveIndex] = useState(0);

  const upperButtons = ["Задачи на сегодня", "Треки", "Спринты", "Питомцы"]
  const lowerButtons = ["Пропущенные задачи", "Настройки"]

  const handleClick = (index: number) => setActiveIndex(index);

  return (
    <nav className="bg-secondary w-55 box-content border-r border-r-secondary-dark">
        <div className="flex flex-col h-4/6 border-b border-b-secondary-dark items-center gap-2 p-2.5">
        {upperButtons.map((btn, index) => (
            <SideBarButton
            key={index}
            label={btn}
            onClick={() => handleClick(index)}
            isActive={activeIndex === index}
            />
        ))}
        </div>
        <div className="flex flex-col items-center gap-2 p-2.5">
        {lowerButtons.map((btn, index) => (
            <SideBarButton
            key={upperButtons.length + index}
            label={btn}
            onClick={() => handleClick(upperButtons.length + index)}
            isActive={activeIndex === upperButtons.length + index}
            />
        ))}
        </div>
    </nav>
  )
}