const BASE_URL = '/api/v1';

export async function apiRequest<T>(endpoint: string, options: RequestInit = {}): Promise<T> {
  const token = localStorage.getItem('aether_token');

  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
    ...(options.headers as Record<string, string>),
  };

  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }

  const response = await fetch(`${BASE_URL}${endpoint}`, {
    ...options,
    headers,
  });

  if (response.status === 401 && !endpoint.includes('/auth/login') && !endpoint.includes('/auth/register')) {
    localStorage.removeItem('aether_token');
    window.location.href = '/login';
    throw new Error('Session expired. Please log in again.');
  }

  const json = await response.json();
  if (!response.ok || json.success === false) {
    throw new Error(json.message || 'An unexpected API error occurred');
  }

  return json.data;
}
