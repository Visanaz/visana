import { Routes } from '@angular/router';
import { linkedIdentityGuard } from './features/session';
import { ApplicationShellPage } from './pages/application-shell/application-shell.page';
import { HomePage } from './pages/home/home.page';
import { StatusPage } from './pages/status/status.page';

export const routes: Routes = [
  { path: '', component: HomePage, title: 'VISANA Plan 3' },
  { path: 'app', canMatch: [linkedIdentityGuard], component: ApplicationShellPage, title: 'VISANA application' },
  { path: 'unauthorized', component: StatusPage, data: { heading: 'Unauthorized', description: 'Inicie sesión para continuar.' } },
  { path: 'forbidden', component: StatusPage, data: { heading: 'Forbidden', description: 'El backend mantiene la autoridad para autorizar acciones.' } },
  { path: 'unlinked', component: StatusPage, data: { heading: 'Unlinked identity', description: 'La identidad autenticada no tiene un actor de plataforma enlazado.' } },
  { path: '**', redirectTo: '' },
];
