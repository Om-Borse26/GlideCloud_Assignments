const API_BASE = import.meta.env.VITE_API_BASE ?? '/api/users';

// Internal request with a specific base
async function doFetch(base, path = '', options = {}) {
  const isGet = !options.method || options.method.toUpperCase() === 'GET';
  const headers = {
    Accept: 'application/json, text/plain, */*',
    ...(isGet ? {} : { 'Content-Type': 'application/json' }),
    ...(options.headers || {})
  };

  const res = await fetch(`${base}${path}`, {
    ...options,
    headers,
    cache: 'no-store'
  });

  const contentType = res.headers.get('content-type') || '';
  let payload;
  try {
    payload = contentType.includes('application/json') ? await res.json() : await res.text();
  } catch {
    payload = '';
  }

  return { ok: res.ok, status: res.status, payload };
}

// Generic fetch helper with proxy-to-fallback resilience
async function request(path = '', options = {}) {
  const isGet = !options.method || options.method.toUpperCase() === 'GET';
  const headers = {
    Accept: 'application/json, text/plain, */*',
    ...(isGet ? {} : { 'Content-Type': 'application/json' }),
    ...(options.headers || {})
  };

  const res = await fetch(`${API_BASE}${path}`, {
    ...options,
    headers,
    cache: 'no-store'
  });

  const contentType = res.headers.get('content-type') || '';
  let payload;
  try {
    payload = contentType.includes('application/json') ? await res.json() : await res.text();
  } catch {
    payload = '';
  }

  if (!res.ok) {
    const message =
      typeof payload === 'string'
        ? payload
        : payload?.message || payload?.error || `Backend error: HTTP ${res.status}`;

    if (isGet && path === '') {
      return { __error: message, __status: res.status, data: [] };
    }
    throw new Error(message);
  }

  return payload;
}

// Safe list fetch: returns array always, plus optional error meta
export const getAllUsers = async () => {
  const result = await request('');
  if (result && typeof result === 'object' && Array.isArray(result.data) && result.__error) {
    return result; // { __error, __status, data: [] }
  }
  return Array.isArray(result) ? result : [];
};

export const getUserById = async (id) => request(`/${id}`);
export const createUser = async (user) =>
  request('', { method: 'POST', body: JSON.stringify(user) });
export const updateUser = async (id, user) =>
  request(`/${id}`, { method: 'PUT', body: JSON.stringify(user) });
export const deleteUser = async (id) =>
  request(`/${id}`, { method: 'DELETE' });
