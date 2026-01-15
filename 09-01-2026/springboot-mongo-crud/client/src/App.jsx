import React, { useEffect, useState } from 'react';
import { getAllUsers, createUser, updateUser, deleteUser } from './services/api';
import UserList from './components/UserList';
import UserForm from './components/UserForm';
import './App.css';

function App() {
  const [users, setUsers] = useState([]);
  const [editingUser, setEditingUser] = useState(null);
  const [loading, setLoading] = useState(false);
  const [err, setErr] = useState(null);

  const fetchUsers = async () => {
    try {
      setLoading(true);
      setErr(null);
      const data = await getAllUsers();

      // Handle safe error shape from getAllUsers
      if (data && typeof data === 'object' && Array.isArray(data.data) && data.__error) {
        setUsers(data.data);
        setErr(`Failed to fetch users: ${data.__error}`);
      } else {
        setUsers(Array.isArray(data) ? data : []);
      }
    } catch (e) {
      console.error(e);
      setErr(`Failed to fetch users: ${e.message || 'Unknown error'}`);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchUsers();
  }, []);

  const onCreate = async (payload) => {
    try {
      setErr(null);
      await createUser(payload);
      await fetchUsers();
    } catch (e) {
      console.error(e);
      setErr(`Failed to create user: ${e.message || 'Unknown error'}`);
    }
  };

  const onUpdate = async (id, payload) => {
    try {
      setErr(null);
      await updateUser(id, payload);
      await fetchUsers();
      setEditingUser(null);
    } catch (e) {
      console.error(e);
      setErr(`Failed to update user: ${e.message || 'Unknown error'}`);
    }
  };

  const onDelete = async (id) => {
    if (!window.confirm('Delete this user?')) return;
    try {
      setErr(null);
      await deleteUser(id);
      await fetchUsers();
    } catch (e) {
      console.error(e);
      setErr(`Failed to delete user: ${e.message || 'Unknown error'}`);
    }
  };

  return (
    <div className="app">
      <header className="app-header">
        <h1>User Management</h1>
        {err && (
          <div className="error-banner">
            {err}
            <button
              className="btn btn-small"
              style={{ marginLeft: 8 }}
              onClick={() => fetchUsers()}
              title="Retry"
            >
              Retry
            </button>
          </div>
        )}
      </header>
      <main className="content">
        <UserForm
          onCreate={onCreate}
          onUpdate={onUpdate}
          editingUser={editingUser}
          onCancel={() => setEditingUser(null)}
        />
        <UserList
          users={users}
          loading={loading}
          onEdit={setEditingUser}
          onDelete={onDelete}
        />
      </main>
    </div>
  );
}

export default App;
