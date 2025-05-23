import { Paper } from "@mui/material";
import { DataGrid, GridColDef } from "@mui/x-data-grid";
import { ruRU } from "@mui/x-data-grid/locales";

interface ViolationsTableProps<T> {
  data: T[] | undefined;
  columns: GridColDef[];
}

const paginationModel = { page: 0, pageSize: 5 };

const ViolationsTable = <T,>({ data, columns }: ViolationsTableProps<T>) => {
  return (
    <Paper sx={{ height: 900, width: "100%" }}>
      <DataGrid
        rows={data}
        columns={columns}
        initialState={{ pagination: { paginationModel } }}
        pageSizeOptions={[5, 10]}
        sx={{ border: 0 }}
        localeText={ruRU.components.MuiDataGrid.defaultProps.localeText}
      />
    </Paper>
  );
};

export default ViolationsTable;
