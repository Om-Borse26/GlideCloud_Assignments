import React from 'react';

function UserList({ users = [], loading = false, onEdit, onDelete }) {
  if (loading) return <section className="card"><div className="loading">Loading users...</div></section>;
  if (!users.length) return <section className="card"><div className="empty">No users found.</div></section>;

  return (
    <section className="card">
      <h2 className="card-title">User List ({users.length})</h2>
      <ul className="list">
        {users.map((u) => (
          <li key={u.id} className="list-item">
            <div className="item-info">
              <strong>{u.name}</strong>
              <span className="muted"> {u.email}</span>
              <span className="muted"> • {u.age}</span>
            </div>
            <div className="item-actions">
              <button className="btn btn-small" onClick={() => onEdit(u)}>Edit</button>
              <button className="btn btn-small btn-danger" onClick={() => onDelete(u.id)}>Delete</button>
            </div>
          </li>
        ))}
      </ul>
    </section>
  );
}

export default UserList;
