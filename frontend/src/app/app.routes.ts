import { Routes } from '@angular/router';
import { UserList } from './features/users/user-list/user-list';
import { UserForm } from './features/users/user-form/user-form';
import { UserDetail } from './features/users/user-detail/user-detail';
import { TicketForm } from './features/tickets/ticket-form/ticket-form';

export const routes: Routes = [
    { path: "user", component: UserList },
    { path: "userDetail", component: UserDetail},
    { path: "userAddForm", component: UserForm },
    { path: 'createTicket', component: TicketForm }
];
