import { useMutation } from "react-query";
import { Button } from "./ui/Button";
import { postApiLogout } from "@/lib/api/generated";
import { useNavigate } from "react-router";

const LogoutButton = () => {
  const route = useNavigate();
  const mutation = useMutation({
    mutationKey: "logout",
    mutationFn: postApiLogout,
    onSuccess: () => {
      route("/login");
    },
  });
  return <Button onClick={() => mutation.mutate({})}>Выйти</Button>;
};

export default LogoutButton;
