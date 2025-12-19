interface FormButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  isLoading?: boolean;
}

export const FormButton = ({ isLoading, children, ...props }: FormButtonProps) => {
  return (
    <button
      className="bg-primary text-secondary rounded-xl h-10 cursor-pointer"
      disabled={isLoading || props.disabled}
      {...props}
    >
        {children}
    </button>
  );
}