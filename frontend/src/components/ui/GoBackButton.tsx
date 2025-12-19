import { ArrowLeft } from 'lucide-react';
import React from 'react';
import { Link } from 'react-router';

interface GoBackButtonProps {
    children: React.ReactNode;
    href: string;
}

export const GoBackButton = ({children, href }: GoBackButtonProps) => {
  return (
    <Link className="flex text-primary" to={href}>
        <ArrowLeft/>
        <p>{children}</p>
    </Link> 
  );
}
