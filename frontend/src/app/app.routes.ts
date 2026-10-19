import { Routes } from '@angular/router';
import { UserList } from './features/users/user-list/user-list';
import { UserForm } from './features/users/user-form/user-form';
import { UserDetail } from './features/users/user-detail/user-detail';
import { TicketForm } from './features/tickets/ticket-form/ticket-form';
import { SignupForm } from './features/signup-form/signup-form';
import { LoginForm } from './features/login-form/login-form';
import { MainLayout } from './core/layout/main-layout/main-layout';
import { HomePage } from './features/home/home-page/home-page';

export const routes: Routes = [
    {
        path: '',
        component: MainLayout,
        children: [
            { path: '', component: HomePage },
            { path: 'userlist', component: UserList },
            { path: 'userdetail/:id', component: UserDetail },
            { path: 'useraddform', component: UserForm },
            { path: 'createticket', component: TicketForm },
            { path: 'signup', component: SignupForm },
            { path: 'login', component: LoginForm },
            { path: '**', redirectTo: '' }
        ]
    }
];
