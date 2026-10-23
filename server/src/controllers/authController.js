import bcrypt from 'bcryptjs';
import jwt from 'jsonwebtoken';
import { pool } from '../config/db.js';
const publicUser = (u) => ({ id: u.id, name: u.name, email: u.email, role: u.role, createdAt: u.created_at });
function tokenFor(user) { return jwt.sign({ id: user.id, name: user.name, email: user.email, role: user.role }, process.env.JWT_SECRET, { expiresIn: '12h' }); }
export async function login(req, res, next) {
  try {
    const email = String(req.body.email || '').trim().toLowerCase();
    const password = String(req.body.password || '');
    if (!email || !password) return res.status(400).json({ message: 'Email and password are required.' });
    const result = await pool.query('SELECT * FROM users WHERE email=$1', [email]);
    const user = result.rows[0];
    if (!user || !(await bcrypt.compare(password, user.password_hash))) return res.status(401).json({ message: 'Invalid email or password.' });
    res.json({ token: tokenFor(user), user: publicUser(user) });
  } catch (e) { next(e); }
}
export async function register(req, res, next) {
  try {
    const name = String(req.body.name || '').trim();
    const email = String(req.body.email || '').trim().toLowerCase();
    const password = String(req.body.password || '');
    if (name.length < 2 || !/^\S+@\S+\.\S+$/.test(email) || password.length < 8) return res.status(400).json({ message: 'Enter a name, valid email, and password with at least 8 characters.' });
    const hash = await bcrypt.hash(password, 12);
    const result = await pool.query("INSERT INTO users(name,email,password_hash,role) VALUES($1,$2,$3,'USER') RETURNING *", [name,email,hash]);
    const user = result.rows[0];
    res.status(201).json({ token: tokenFor(user), user: publicUser(user) });
  } catch (e) { if (e.code === '23505') return res.status(409).json({ message: 'An account with this email already exists.' }); next(e); }
}
export async function me(req,res,next) { try { const r=await pool.query('SELECT id,name,email,role,created_at FROM users WHERE id=$1',[req.user.id]); if(!r.rowCount) return res.status(404).json({message:'Account not found.'}); res.json({user:publicUser(r.rows[0])}); } catch(e){next(e);} }
