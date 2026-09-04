import { Component, inject } from '@angular/core';
import { RuntimeConfigService } from '../../core/config/runtime-config.service';
import { SessionFacade } from '../../features/session';

@Component({
  selector: 'app-home-page',
  template: `
    <section aria-labelledby="foundation-heading" class="bg-surface mx-auto max-w-3xl rounded-lg border border-border p-8 shadow-sm">
      <p class="text-muted text-sm">Foundation F0</p>
      <h1 id="foundation-heading" class="text-primary mt-2 text-3xl font-semibold">VISANA Plan 3 frontend</h1>
      <p class="text-muted mt-4">La aplicación espera configuración de entorno y contratos OpenAPI aprobados antes de habilitar journeys de producto.</p>
      <dl class="mt-6 grid gap-4 sm:grid-cols-2">
        <div><dt class="text-muted text-sm">Runtime configuration</dt><dd class="text-foreground font-medium">{{ runtimeConfig.status() }}</dd></div>
        <div><dt class="text-muted text-sm">Session foundation</dt><dd class="text-foreground font-medium">{{ session.state().phase }}</dd></div>
      </dl>
    </section>
  `,
})
export class HomePage {
  protected readonly runtimeConfig = inject(RuntimeConfigService);
  protected readonly session = inject(SessionFacade);
}
