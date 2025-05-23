import React from "react";
import { QueryClientProvider } from "react-query";
import { Route, Routes } from "react-router";
import HomePage from "./pages/HomePage";
import Layout from "./components/Layout";
import Header from "./components/Header";
import LoginPage from "./pages/LoginPage";
import RegistrationPage from "./pages/RegistrationPage";
import ChartPage from "./pages/ChartPage";
import { queryClient } from "./lib/api/queryClient";
import ProtectedRoute from "./route/ProtectedRoute";

import { ToastContainer } from "react-toastify";

const App: React.FC = () => {
  return (
    <QueryClientProvider client={queryClient}>
      <Routes>
        {/* Public Routes */}
        <Route path="/login" element={<LoginPage />} />
        <Route path="/registration" element={<RegistrationPage />} />

        {/* Protected Routes */}
        <Route
          element={
            <ProtectedRoute>
              <Layout header={<Header />} />
            </ProtectedRoute>
          }
        >
          <Route path="/" element={<HomePage />} />
          <Route path="/chart" element={<ChartPage />} />
        </Route>
      </Routes>
      <ToastContainer theme="colored" />
    </QueryClientProvider>
  );
};

export default App;
