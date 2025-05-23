# Используем официальный образ Node.js 21
FROM node:21

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем package.json и package-lock.json для кеширования зависимости
COPY package.json package-lock.json ./

# Устанавливаем зависимости
RUN npm install

# Копируем остальные файлы проекта
COPY . .

# Собираем проект
RUN npm run build

# Открываем порт для разработки
EXPOSE 5173

# Запуск в режиме разработки
CMD ["npm", "run", "dev"]
