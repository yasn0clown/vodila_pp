import React from "react";
import { Bar } from "react-chartjs-2";
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend,
} from "chart.js";
import { Violations } from "@/lib/api/generated";

ChartJS.register(
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend
);

interface ViolationChartsProps {
  data: Violations[] | undefined;
}

const ViolationCharts: React.FC<ViolationChartsProps> = ({ data }) => {
  const groupedData = data
    ? data.reduce((acc, violation) => {
        if (violation.violation_type)
          acc[violation.violation_type] =
            (acc[violation.violation_type] || 0) + 1;
        return acc;
      }, {} as Record<string, number>)
    : {};

  const chartData = {
    labels: Object.keys(groupedData),
    datasets: [
      {
        label: "Количество нарушений",
        data: Object.values(groupedData),
        backgroundColor: "rgba(124, 77, 15, 0.2)",
        borderColor: "rgb(5, 153, 42)",
        borderWidth: 1,
      },
    ],
  };

  return <Bar data={chartData} />;
};

export default ViolationCharts;
