interface SideBarButtonProps {
  isActive: boolean;
  label: string;
  onClick: () => void;
}

export const SideBarButton = ({isActive, label, onClick}: SideBarButtonProps) => {
  return (
    <button
        className={`w-50 h-7.5 rounded-lg cursor-pointer
            ${
            isActive
                ? "bg-primary text-secondary"
                : "text-primary-dark hover:bg-secondary-dark"
            }`}
            onClick={onClick}
        >
        {label}
    </button>
  )
}