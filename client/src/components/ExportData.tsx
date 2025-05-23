import React from "react";
import { Button } from "./ui/Button";
import { Violations } from "@/lib/api/generated";

interface ExportDataProps {
  data: Violations[] | undefined;
}

const ExportData: React.FC<ExportDataProps> = ({ data }) => {
  const handleExport = () => {
    const csvContent = data
      ? "data:text/csv;charset=utf-8," +
        ["ID;Type;Vehicle Number;Date"]
          .concat(
            data.map(
              (violation) =>
                `${violation.id};${violation.track_id}${violation.violation_type};${violation.violation_description};${violation.filepath}`
            )
          )
          .join("\n")
      : "";
    const encodedUri = encodeURI(csvContent);
    const link = document.createElement("a");
    link.setAttribute("href", encodedUri);
    link.setAttribute("download", "violations.csv");
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };

  return <Button onClick={handleExport}>Экспортировать в CSV</Button>;
};

export default ExportData;
