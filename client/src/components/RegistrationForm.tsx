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
import { Link } from "react-router";
import { useMutation } from "react-query";
import { postApiRegister } from "@/lib/api/generated";
import { toast } from "react-toastify";

const formSchema = z
  .object({
    email: z
      .string({ message: "Это обязательное поле" })
      .email("Неверный email"),
    username: z.string({ message: "Это обязательное поле" }).min(3, {
      message: "Минимум 3 символа",
    }),
    password: z.string({ message: "Это обязательное поле" }).min(6, {
      message: "Минимум 6 символом",
    }),
    repeatPassword: z.string({ message: "Это обязательное поле" }).min(6, {
      message: "Минимум 6 символом",
    }),
  })
  .superRefine(({ repeatPassword, password }, ctx) => {
    if (repeatPassword !== password) {
      ctx.addIssue({
        code: "custom",
        message: "Пароли не совпадают",
        path: ["repeatPassword"],
      });
    }
  });

const RegistrationForm = () => {
  const mutation = useMutation({
    mutationKey: "register",
    mutationFn: postApiRegister,
    onError: () => {
      toast("Внезапная ошибка.", { type: "error" });
    },
  });

  const form = useForm<z.infer<typeof formSchema>>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      email: "",
      username: "",
      password: "",
      repeatPassword: "",
    },
  });

  const onSubmit: SubmitHandler<z.infer<typeof formSchema>> = (data) => {
    const { repeatPassword, ...mutationData } = data;
    mutation.mutate(mutationData);
  };

  return (
    <Card className="w-[350px]">
      <CardHeader>
        <CardTitle>Зарегистрируйтесь</CardTitle>
      </CardHeader>
      <CardContent className="space-y-2">
        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
            <FormField
              control={form.control}
              name="email"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Email</FormLabel>
                  <FormControl>
                    <Input placeholder="Введите e-mail" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
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
            <FormField
              control={form.control}
              name="repeatPassword"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Повторите пароль</FormLabel>
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
                <Link to="/login">Авторизоваться</Link>
              </Button>
              <Button type="submit">Отправить</Button>
            </div>
          </form>
        </Form>
      </CardContent>
    </Card>
  );
};

export default RegistrationForm;
