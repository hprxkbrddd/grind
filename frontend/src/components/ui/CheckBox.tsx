import { Square, SquareCheck } from 'lucide-react';
import { useState } from 'react';

export const CheckBox = () => {
  const [checked, setChecked] = useState(false);

  return (
    <label className="cursor-pointer">
      <input
        type="checkbox"
        className="hidden"
        checked={checked}
        onChange={() => setChecked(!checked)}
      />
      {checked ? <SquareCheck className="text-primary-dark"/> : <Square className="text-primary-dark"/>}
    </label>
  );
}