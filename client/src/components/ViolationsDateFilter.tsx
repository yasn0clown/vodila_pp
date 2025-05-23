import { ChangeEvent } from "react";

const ViolationsDateFilter = ({
  onStartDateChange,
  onEndDateChange,
}: {
  onStartDateChange: (e: ChangeEvent<HTMLInputElement>) => void;
  onEndDateChange: (e: ChangeEvent<HTMLInputElement>) => void;
}) => {
  return (
    <div>
      <input type="date" onChange={onStartDateChange} />
      <input type="date" onChange={onEndDateChange} />
    </div>
  );
};

export default ViolationsDateFilter;
