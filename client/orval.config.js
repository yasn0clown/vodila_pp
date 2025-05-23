module.exports = {
  main: {
    input: "./src/lib/api/schema.json",
    output: {
      target: "./src/lib/api/generated.ts",
      prettier: true,
      override: {
        mutator: {
          path: "./src/lib/api/index.ts",
          name: "createInstance",
        },
      },
    },
  },
};
