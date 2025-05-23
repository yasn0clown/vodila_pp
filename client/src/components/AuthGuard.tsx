import { PropsWithChildren, ReactElement } from "react";
import { useQuery } from "react-query";
import { getApiUserInfo } from "@/lib/api/generated";
import { useNavigate } from "react-router";

function authGuard<P>(Component: (props: P) => ReactElement) {
  return function AuthGuard(props: PropsWithChildren<P>) {
    const navigate = useNavigate();

    const { data, isError } = useQuery("session", getApiUserInfo);

    if (data) return <Component {...props} />;

    if (isError) navigate("/login");
  };
}

export default authGuard;
