import { Box, Paper, styled } from "@mui/material";
import ViolationCharts from "@/components/ViolationCharts";
import { getApiViolations } from "@/lib/api/generated";
import { useQuery } from "react-query";

// Стили для геометрического фона (аналогичные главной странице)
const GeometricBackground = styled("div")({
  position: "fixed",
  top: 0,
  left: 0,
  width: "100%",
  height: "100%",
  zIndex: -1,
  overflow: "hidden",
  backgroundColor: "#f8fafc",
  "& > div": {
    position: "absolute",
    filter: "blur(60px)",
    opacity: 0.8,
    animation: "float 20s infinite linear",
    mixBlendMode: "multiply",
  },
});

const Circle1 = styled("div")({
  width: "500px",
  height: "500px",
  top: "10%",
  left: "5%",
  backgroundColor: "#93c5fd",
  borderRadius: "50%",
  animationDelay: "0s",
});

const Circle2 = styled("div")({
  width: "600px",
  height: "600px",
  bottom: "10%",
  right: "5%",
  backgroundColor: "#fca5a5",
  borderRadius: "50%",
  animationDelay: "5s",
});

const Square = styled("div")({
  width: "400px",
  height: "400px",
  top: "50%",
  left: "70%",
  backgroundColor: "#86efac",
  borderRadius: "25px",
  animation: "rotate 30s infinite linear",
  transform: "rotate(15deg)",
});

const Triangle = styled("div")({
  width: 0,
  height: 0,
  top: "30%",
  left: "10%",
  borderLeft: "200px solid transparent",
  borderRight: "200px solid transparent",
  borderBottom: "400px solid #d8b4fe",
  animation: "pulse 15s infinite alternate",
});

const floatAnimation = {
  "@keyframes float": {
    "0%": { transform: "translate(0, 0) rotate(0deg)" },
    "25%": { transform: "translate(50px, -80px) rotate(90deg)" },
    "50%": { transform: "translate(100px, 0) rotate(180deg)" },
    "75%": { transform: "translate(50px, 80px) rotate(270deg)" },
    "100%": { transform: "translate(0, 0) rotate(360deg)" },
  },
};

const rotateAnimation = {
  "@keyframes rotate": {
    "0%": { transform: "rotate(15deg) scale(1)" },
    "50%": { transform: "rotate(195deg) scale(1.2)" },
    "100%": { transform: "rotate(375deg) scale(1)" },
  },
};

const pulseAnimation = {
  "@keyframes pulse": {
    "0%": { transform: "translateY(0) scale(1)", opacity: 0.7 },
    "50%": { transform: "translateY(-100px) scale(1.1)", opacity: 0.9 },
    "100%": { transform: "translateY(0) scale(1)", opacity: 0.7 },
  },
};

const ChartPage = () => {
  const { data: violations } = useQuery("violationsData", getApiViolations);

  return (
    <Box
      sx={{
        position: "relative",
        minHeight: "100vh",
        padding: "2rem",
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
        overflow: "hidden",
      }}
    >
      {/* Яркие геометрические фигуры */}
      <GeometricBackground>
        <Circle1 sx={floatAnimation} />
        <Circle2 sx={floatAnimation} />
        <Square sx={rotateAnimation} />
        <Triangle sx={pulseAnimation} />
      </GeometricBackground>

      {/* Основное содержимое */}
      <Paper
        elevation={6}
        sx={{
          width: "95%",
          maxWidth: "1200px",
          borderRadius: "16px",
          overflow: "hidden",
          backgroundColor: "rgba(255, 255, 255, 0.97)",
          boxShadow: "0 8px 32px 0 rgba(31, 38, 135, 0.25)",
          position: "relative",
          zIndex: 1,
          backdropFilter: "blur(4px)",
          border: "1px solid rgba(255, 255, 255, 0.3)",
          p: 3,
        }}
      >
        <ViolationCharts data={violations} />
      </Paper>
    </Box>
  );
};

export default ChartPage;
