import express from 'express';import cors from 'cors';import helmet from 'helmet';import dotenv from 'dotenv';
import authRoutes from './routes/auth.js';import ticketRoutes from './routes/tickets.js';import miscRoutes from './routes/misc.js';
dotenv.config();const app=express();app.use(helmet());app.use(cors({origin:process.env.CLIENT_URL?.split(',')||'http://localhost:5173'}));app.use(express.json({limit:'1mb'}));
app.get('/api/health',(req,res)=>res.json({status:'ok',service:'Ticket Management API'}));app.use('/api/auth',authRoutes);app.use('/api/tickets',ticketRoutes);app.use('/api',miscRoutes);
app.use((req,res)=>res.status(404).json({message:'API endpoint not found.'}));app.use((err,req,res,next)=>{console.error(err);if(res.headersSent)return next(err);res.status(500).json({message:process.env.NODE_ENV==='production'?'Unexpected server error.':err.message||'Unexpected server error.'})});export default app;
