import { SubmitHandler, useForm } from "react-hook-form";
import { Button } from "./ui/Button";
import { Card, CardContent, CardHeader, CardTitle } from "./ui/Card";
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "./ui/Form";
import { Input } from "./ui/Input";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { Link, useNavigate } from "react-router";
import { useMutation } from "react-query";
import { postApiLogin } from "@/lib/api/generated";
import { toast } from "react-toastify";
import { AxiosError } from "axios";

const formSchema = z.object({
  username: z.string({ message: "Это обязательное поле" }).min(3, {
    message: "Заполните поле",
  }),
  password: z.string({ message: "Это обязательное поле" }).min(6, {
    message: "Заполните поле",
  }),
});

const LoginForm = () => {
  const route = useNavigate();
  const mutation = useMutation({
    mutationKey: "login",
    mutationFn: postApiLogin,
    onError: (e) => {
      if (e instanceof AxiosError) {
        if (e.status === 401)
          toast("Пользователь с таким никнеймом не найден", { type: "error" });
      }
      toast("Неожиданная ошибка", { type: "error" });
    },
    onSuccess: () => {
      toast("Вы успешно авторизовались", { type: "success" });
      route("/");
    },
  });

  const form = useForm<z.infer<typeof formSchema>>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      username: "",
      password: "",
    },
  });
  const onSubmit: SubmitHandler<z.infer<typeof formSchema>> = (data) =>
    mutation.mutate(data);

  return (
    <Card className="w-[350px]">
      <CardHeader>
        <CardTitle>Авторизуйтесь</CardTitle>
      </CardHeader>
      <CardContent className="space-y-2">
        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
            <FormField
              control={form.control}
              name="username"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Никнейм</FormLabel>
                  <FormControl>
                    <Input placeholder="Введите никнейм" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="password"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Пароль</FormLabel>
                  <FormControl>
                    <Input
                      type="password"
                      placeholder="Введите пароль..."
                      {...field}
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <div className="flex justify-between">
              <Button asChild variant="outline">
                <Link to="/registration">Зарегистрироваться</Link>
              </Button>
              <Button type="submit">Отправить</Button>
            </div>
          </form>
        </Form>
      </CardContent>
    </Card>
  );
};

export default LoginForm;
