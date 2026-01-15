import React, { useEffect, useState } from 'react';

function UserForm({ onCreate, onUpdate, editingUser, onCancel }) {
  const [form, setForm] = useState({ name: '', email: '', age: '' });

  useEffect(() => {
    if (editingUser) {
      setForm({
        name: editingUser.name ?? '',
        email: editingUser.email ?? '',
        age: editingUser.age?.toString() ?? '',
      });
    } else {
      setForm({ name: '', email: '', age: '' });
    }
  }, [editingUser]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((f) => ({ ...f, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const payload = { ...form, age: Number.parseInt(form.age, 10) };
    if (editingUser) {
      await onUpdate(editingUser.id, payload);
    } else {
      await onCreate(payload);
    }
    setForm({ name: '', email: '', age: '' });
  };

  return (
    <section className="card">
      <h2 className="card-title">{editingUser ? 'Edit User' : 'Add User'}</h2>
      <form onSubmit={handleSubmit} className="form">
        <label className="form-group">
          <span>Name</span>
          <input name="name" value={form.name} onChange={handleChange} placeholder="Name" required />
        </label>
        <label className="form-group">
          <span>Email</span>
          <input name="email" type="email" value={form.email} onChange={handleChange} placeholder="Email" required />
        </label>
        <label className="form-group">
          <span>Age</span>
          <input name="age" type="number" value={form.age} onChange={handleChange} placeholder="Age" required />
        </label>
        <div className="actions">
          <button type="submit" className="btn btn-primary">{editingUser ? 'Update' : 'Add'}</button>
          {editingUser && <button type="button" className="btn" onClick={onCancel}>Cancel</button>}
        </div>
      </form>
    </section>
  );
}

export default UserForm;
