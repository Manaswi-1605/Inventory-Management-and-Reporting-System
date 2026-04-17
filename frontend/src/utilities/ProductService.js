import { apiRequest } from "./api";

const ProductService = {
  getAllProducts: () => apiRequest("/api/products"),

  addProduct: (product) =>
    apiRequest("/api/product", "POST", product),

  increaseStock: (id, qty) =>
    apiRequest(`/api/increase/${id}/${qty}`, "POST"),

  reduceStock: (id, qty) =>
    apiRequest(`/api/reduce/${id}/${qty}`, "POST"),
};

export default ProductService;