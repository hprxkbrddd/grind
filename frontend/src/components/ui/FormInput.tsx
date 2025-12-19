import React from 'react';

export const FormInput = ({children, ...props }: React.InputHTMLAttributes<HTMLInputElement>) => {
  return (
    <div className="flex flex-col">
      <label htmlFor={props.id}>{children}</label>
      <input className="border border-brand-dark rounded-xl h-10 bg-secondary-dark px-2"
      {...props}
      required/>
    </div>
  );
}