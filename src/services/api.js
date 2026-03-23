import axios from 'axios';

const API_BASE = 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE,
  headers: { 'Content-Type': 'application/json' },
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

// Auth
export const registerUser = (data) => api.post('/auth/register', data);
export const loginUser    = (data) => api.post('/auth/login', data);

// Movies
export const getAllMovies          = ()       => api.get('/movies');
export const getMovieById          = (id)     => api.get(`/movies/${id}`);
export const searchMovies          = (params) => api.get('/movies/search', { params });
export const getLatestMovies       = ()       => api.get('/movies/latest');          // NEW
export const getMoviesByMonth      = (month, year) =>                                // NEW
  api.get('/movies/by-month', { params: { month, year } });
export const addMovie              = (data)   => api.post('/movies', data);
export const updateMovie           = (id, data) => api.put(`/movies/${id}`, data);
export const deleteMovie           = (id)     => api.delete(`/movies/${id}`);

// Reviews
export const getReviewsByMovie = (movieId)       => api.get(`/reviews/movie/${movieId}`);
export const addReview         = (movieId, data) => api.post(`/reviews/movie/${movieId}`, data);

export default api;