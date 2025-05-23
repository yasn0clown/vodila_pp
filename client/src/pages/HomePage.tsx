import { ChangeEvent, useState } from "react";
import ViolationsTable from "../components/ViolationsTable";
import ViolationTypeFilters from "../components/ViolationTypeFilter";
import ExportData from "../components/ExportData";
import { GridColDef, GridRenderCellParams } from "@mui/x-data-grid";
import Button from "@mui/material/Button";
import { useMutation, useQuery } from "react-query";
import {
  getApiUserInfo,
  getApiViolations,
  postApiUploadViolation,
  getApiUploadsFilename,
} from "@/lib/api/generated";
import { UploadButton } from "@/components/ui/UploadButton";
import { toast } from "react-toastify";
import { Box, Typography, Paper, styled } from "@mui/material";

// Стили для геометрического фона
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

// Большие цветные круги
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

// Яркий квадрат
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

// Крупный треугольник
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

// Анимации
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

const columns: GridColDef[] = [
  {
    field: "id",
    headerName: "ID",
    width: 70,
    headerClassName: "table-header",
  },
  {
    field: "id_User",
    headerName: "ID пользователя",
    width: 70,
    headerClassName: "table-header",
  },
  {
    field: "violation_type",
    headerName: "Тип",
    width: 130,
    headerClassName: "table-header",
  },
  {
    field: "violation_description",
    headerName: "Описание",
    width: 400,
    headerClassName: "table-header",
  },
  {
    field: "filepath",
    headerName: "Изображение",
    width: 300,
    headerClassName: "table-header",
    renderCell: (params: GridRenderCellParams) => {
      const filename = params.value;
      const [imageUrl, setImageUrl] = useState<string | null>(null);
      const [isLoading, setIsLoading] = useState(false);

      const handleClick = async () => {
        setIsLoading(true);
        try {
          const imageBlob = await getApiUploadsFilename(filename, {
            responseType: "blob",
          });
          setImageUrl(URL.createObjectURL(imageBlob));
        } catch (error) {
          console.error("Ошибка загрузки изображения:", error);
        } finally {
          setIsLoading(false);
        }
      };

      return (
        <Button
          variant="contained"
          onClick={handleClick}
          disabled={isLoading}
          sx={{
            backgroundColor: "#3f51b5",
            color: "white",
            "&:hover": {
              backgroundColor: "#303f9f",
              transform: "translateY(-2px)",
              boxShadow: "0 4px 8px rgba(0,0,0,0.2)",
            },
            transition: "all 0.3s ease",
          }}
        >
          {isLoading ? "Загрузка..." : imageUrl ? "Просмотр" : "Скачать"}
        </Button>
      );
    },
  },
];

const HomePage = () => {
  const { data: user } = useQuery("session", getApiUserInfo);
  const { data: violations } = useQuery("violationsData", getApiViolations);

  const mutation = useMutation({
    mutationKey: "uploadViolation",
    mutationFn: postApiUploadViolation,
    onSuccess: () => {
      toast.success("Файл успешно обработан");
    },
  });

  const [typeFilter, setTypeFilter] = useState("all");

  const handleTypeFilterChange = (filter: string) => {
    setTypeFilter(filter);
  };

  const onUpload = (e: ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files && e.target.files[0];
    if (file)
      mutation.mutate({ file: file, vehicle_number: "", violation_type: "" });
  };

  const filteredViolations = violations?.filter(
    (violation) =>
      typeFilter === "all" || violation?.violation_type === typeFilter
  );

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
          maxWidth: "1800px",
          borderRadius: "16px",
          overflow: "hidden",
          backgroundColor: "rgba(255, 255, 255, 0.97)",
          boxShadow: "0 8px 32px 0 rgba(31, 38, 135, 0.25)",
          position: "relative",
          zIndex: 1,
          backdropFilter: "blur(4px)",
          border: "1px solid rgba(255, 255, 255, 0.3)",
        }}
      >
        <Box sx={{ padding: "2rem" }}>
          <Typography
            variant="h3"
            sx={{
              fontWeight: 700,
              marginBottom: "2rem",
              textAlign: "center",
              color: "#1e293b",
              position: "relative",
              "&::after": {
                content: '""',
                display: "block",
                width: "100px",
                height: "4px",
                backgroundColor: "#3b82f6",
                margin: "0.5rem auto 0",
                borderRadius: "2px",
              },
            }}
          >
            Панель управления нарушениями
          </Typography>

          <Box
            sx={{
              display: "flex",
              flexDirection: { xs: "column", md: "row" },
              justifyContent: "space-between",
              alignItems: "center",
              gap: "1.5rem",
              marginBottom: "2rem",
              padding: "1.5rem",
              borderRadius: "12px",
              backgroundColor: "rgba(241, 245, 249, 0.7)",
              border: "1px solid rgba(226, 232, 240, 0.5)",
            }}
          >
            <ViolationTypeFilters
              value={typeFilter}
              onValueChange={handleTypeFilterChange}
              sx={{
                flexGrow: 1,
                maxWidth: "400px",
              }}
            />

            <Box
              sx={{
                display: "flex",
                gap: "1rem",
                flexDirection: { xs: "column", sm: "row" },
              }}
            >
              {user?.role === "admin" && (
                <UploadButton
                  text="Загрузить нарушение"
                  onChange={onUpload}
                  sx={{
                    backgroundColor: "#4caf50",
                    "&:hover": {
                      backgroundColor: "#388e3c",
                    },
                  }}
                />
              )}
              <ExportData
                data={filteredViolations}
                sx={{
                  backgroundColor: "#ff9800",
                  "&:hover": {
                    backgroundColor: "#f57c00",
                  },
                }}
              />
            </Box>
          </Box>

          <ViolationsTable
            data={filteredViolations}
            columns={columns}
            sx={{
              "& .table-header": {
                backgroundColor: "#3f51b5",
                color: "white",
                fontWeight: 600,
                fontSize: "0.875rem",
              },
              "& .MuiDataGrid-cell": {
                borderBottom: "1px solid rgba(224, 224, 224, 0.5)",
              },
            }}
          />
        </Box>
      </Paper>
    </Box>
  );
};

export default HomePage;
