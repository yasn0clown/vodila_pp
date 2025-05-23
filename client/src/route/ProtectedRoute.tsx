import { getApiUserInfo } from "@/lib/api/generated";
import { PropsWithChildren } from "react";
import { useQuery } from "react-query";
import { Navigate } from "react-router";

interface IProtectedRoute extends PropsWithChildren {
  redirectPath?: string;
}

const ProtectedRoute = ({
  redirectPath = "/login",
  children,
}: IProtectedRoute) => {
  // const { data, isError, isLoading } = useQuery("session", getApiUserInfo, {
  //   retry: 0,
  // });

  // if (isLoading) {
  //   return <div>Loading...</div>;
  // }

  // if (isError || !data) {
  //   return <Navigate to={redirectPath} replace />;
  // }

  return <>{children}</>;
};

export default ProtectedRoute;
