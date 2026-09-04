import { Component } from '@angular/core';

@Component({
  selector: 'app-application-shell-page',
  template: `
    <section class="bg-surface mx-auto max-w-xl rounded-lg border border-border p-8">
      <h1 class="text-primary text-2xl font-semibold">Application shell</h1>
      <p class="text-muted mt-3">Los módulos de catálogo, órdenes y red se habilitarán sólo mediante contratos aprobados.</p>
    </section>
  `,
})
export class ApplicationShellPage {}
