import { Router } from 'express';
import { authenticate } from '../middleware/auth.js';
import { listTickets,createTicket,getTicket,updateTicket,assignTicket,addComment,submitFeedback } from '../controllers/ticketController.js';
const router=Router();router.use(authenticate);router.get('/',listTickets);router.post('/',createTicket);router.get('/:id',getTicket);router.patch('/:id',updateTicket);router.patch('/:id/assign',assignTicket);router.post('/:id/comments',addComment);router.post('/:id/feedback',submitFeedback);export default router;
