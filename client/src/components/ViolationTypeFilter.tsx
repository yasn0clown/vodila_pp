import React from "react";

import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/Select";

interface ViolationFiltersProps {
  value: string;
  onValueChange: (filter: string) => void;
}

const ViolationTypeFilters: React.FC<ViolationFiltersProps> = ({
  value,
  onValueChange,
}) => {
  return (
    <div className="w-[100%] lg:w-[180px]">
      <Select value={value} onValueChange={onValueChange}>
        <SelectTrigger className="w-[100%]">
          <SelectValue placeholder="Select a fruit" />
        </SelectTrigger>
        <SelectContent>
          <SelectItem value="all">Все нарушения</SelectItem>
          <SelectItem value="Отвлечение от дороги">
            Отвлечение от дороги
          </SelectItem>
          <SelectItem value="Разговор по телефону">
            Разговор по телефону
          </SelectItem>
          <SelectItem value="Засыпание за рулем">Засыпание за рулем</SelectItem>
        </SelectContent>
      </Select>
    </div>
  );
};

export default ViolationTypeFilters;
